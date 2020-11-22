const database = require('./database');

const rowMapper = row => {
    return {
        id: row.id,
        alias: row.user_alias,
        publicKey: row.public_key,
        password: row.password_hash
    }
};

class UserDao {
    getUsers(aliases) {
        return database
            .query('SELECT id, user_alias, public_key, password_hash FROM location_tracker.users WHERE user_alias IN (' +
                aliases.map((alias, idx) => `$${idx + 1}`).join(',') +
                ')', aliases)
            .then(res => res.rows.map(rowMapper));
    }

    getUserById(id) {
        return database
            .query('SELECT id, user_alias, public_key, password_hash FROM location_tracker.users WHERE id = $1', [id])
            .then(res => rowMapper(res.rows[0]));
    }

    createUser(alias, publicKey, password) {
        return database
            .query('INSERT INTO location_tracker.users(user_alias, public_key, password_hash) VALUES ($1, $2, $3) RETURNING *', [
                alias,
                publicKey,
                password
            ])
            .then(res => rowMapper(res.rows[0]));
    }
}

module.exports = new UserDao();