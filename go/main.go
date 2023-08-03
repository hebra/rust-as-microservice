package main

import (
	"encoding/base64"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/jmoiron/sqlx"
	"github.com/joho/godotenv"
	_ "github.com/mattn/go-sqlite3"
	"github.com/nbutton23/zxcvbn-go"
	"log"
	"net/http"
	"net/mail"
	"os"
	"strings"
)

var disablePasswordHashing = false
var db *sqlx.DB

type SignupUser struct {
	Email         string `json:"email"`
	Password      string `json:"password"`
	TermsAccepted bool   `json:"terms_accepted"`
}

type User struct {
	Userid        string `json:"userid"`
	Email         string `json:"email"`
	PasswordHash  string `json:"-"`
	TermsAccepted bool   `json:"terms_accepted"`
}

func createUser(c *gin.Context) {

	var signupUser SignupUser

	if err := c.BindJSON(&signupUser); err != nil {
		c.String(500, err.Error())
		return
	}

	if _, err := mail.ParseAddress(signupUser.Email); err != nil {
		c.String(400, err.Error())
		return
	}

	if zxcvbn.PasswordStrength(signupUser.Password, nil).Score < 3 {
		c.String(400, "Password too weak.")
		return
	}

	var id int
	if err := db.Get(&id, "SELECT count(1) FROM users WHERE email = ?", signupUser.Email); err != nil {
		log.Fatalf("Error querying database: %s", err)
	}

	if id > 0 {
		return
	}

	var userid = base64.StdEncoding.EncodeToString([]byte(uuid.New().String()))

	var password_hash = signupUser.Password

	var user = User{
		Userid:        userid,
		Email:         signupUser.Email,
		PasswordHash:  password_hash,
		TermsAccepted: signupUser.TermsAccepted,
	}

	db.MustExec("INSERT INTO users(userid, email, password_hash, terms_accepted) VALUES(?,?,?,?)",
		userid, user.Email, password_hash, user.TermsAccepted)

	c.IndentedJSON(http.StatusOK, user)

	return
}

func init_db() {
	var schema = `
CREATE TABLE users
(
    userid         VARCHAR(100) PRIMARY KEY,
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    terms_accepted BOOLEAN      NOT NULL DEFAULT false
)
`
	db.MustExec(schema)
	db.MustExec("CREATE UNIQUE INDEX email_addr_idx\nON users(email);\n")
}

func main() {

	if err := godotenv.Load(".env"); err != nil {
		log.Fatalf("Error loading .env file: %s", err)
	}

	var dbUri = os.Getenv("DATABASE_URL")
	disablePasswordHashing = strings.EqualFold(os.Getenv("DISABLE_PASSWORD_HASHING"), "true")

	//var datasourceName = strings.Split(dbUri, ":")[1]
	var datasourceName = ":memory:"

	log.Printf("Using database URI %s, dataSource is %s", dbUri, datasourceName)

	var err error
	if db, err = sqlx.Connect("sqlite3", datasourceName); err != nil {
		log.Fatalf("Error connecting to database: %s", err)
	}

	init_db()

	router := gin.New()
	router.POST("/api/users", createUser)

	router.Run("localhost:8080")
}
