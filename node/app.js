const express = require('express');
const {response} = require("express");
const bodyParser = require('body-parser');
const emailValidator = require('deep-email-validator');
const zxcvbn = require('zxcvbn')
const {v4: uuidv4} = require('uuid');
const sqlite3 = require('sqlite3');

const app = express();
app.use(bodyParser.json());

// let db = new sqlite3.Database(':memory:');
let db = new sqlite3.Database('poc.db');

class SignupUser {
    email = ""
    password = ""
    terms_accepted = false

    constructor(obj) {
        obj && Object.assign(this, obj);
    }
}


app.post('/api/users', (req, res) => {

    const signupUser = new SignupUser(req.body)

    if (!emailValidator.validate(signupUser.email)) {
        res.status(400)
        res.send("Invalid email address format")
        return
    }

    if (zxcvbn(signupUser.password).score < 3) {
        res.status(400)
        res.send("Password too weak")
        return
    }

    db.get("SELECT count(1) as count FROM users", [], (err, row) => {
        if (row.count > 0) {
            res.status(400)
            res.send("User already exists")
            return
        }

        const uuid = uuidv4();
        const userid = Buffer.alloc(uuid.length).fill(uuid).toString('base64')

        const password_hash = signupUser.password

        db.run(`INSERT INTO users(userid, email, password_hash, terms_accepted)
                VALUES (?, ?, ?, ?)`,
            [userid, signupUser.email, password_hash, signupUser.terms_accepted],
            (err) => {
                if (err) {
                    console.error(err)
                    res.status(500)
                    res.send("Error inserting into database")
                    return
                }

                res.json({
                    userid: userid,
                    email: signupUser.email,
                    terms_accepted: signupUser.terms_accepted
                });
                res.send()
            });
    });
});

const port = 3000;
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
