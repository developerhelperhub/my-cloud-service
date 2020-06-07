db.createUser ({
user: "myidsrvdbuser",
pwd: "myidsrvdbpwd@1234",
roles: [{
	role: "readWrite",
	db: "myidsrvdb"
}]
})
