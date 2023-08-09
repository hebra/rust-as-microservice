const emailValidator = require('deep-email-validator');
const zxcvbn = require('zxcvbn')
const {v4: uuidv4} = require('uuid');
const {AsyncDatabase} = require("promised-sqlite3");

class SignupUser {
    email = ""
    password = ""
    terms_accepted = false

    constructor(obj) {
        obj && Object.assign(this, obj);
    }
}

let _db;
const getDb = async () => {
    if (!_db) {
        _db = await AsyncDatabase.open("poc.db");
    }

    return _db;
}

const addUser = async (body) => {
    const signupUser = new SignupUser(body)
    const db = await getDb();
    if (!emailValidator.validate(signupUser.email)) {
        throw new Error("Invalid email address format");
    }

    if (zxcvbn(signupUser.password).score < 3) {
        throw new Error("Password too weak");
    }

    let row;
    try {
        row = await db.get("SELECT count(1) as count FROM users WHERE email=?", [signupUser.email]);
    } catch (error) {
        console.error(error)
        throw new Error("Error counting rows");
    }

    if (row && row.count > 0) {
        throw new Error("User already exists:" + signupUser.email);
    }

    const uuid = uuidv4();
    const userid = Buffer.alloc(uuid.length).fill(uuid).toString('base64')

    const password_hash = signupUser.password

    try {
        await db.run(`INSERT INTO users(userid, email, password_hash, terms_accepted)
                VALUES (?, ?, ?, ?)`,
            [userid, signupUser.email, password_hash, signupUser.terms_accepted])
    } catch (error) {
        throw new Error("Error inserting into database");
    }

    return {
        userid: userid,
        email: signupUser.email,
        terms_accepted: signupUser.terms_accepted
    }
}


module.exports = {addUser}