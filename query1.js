// Query 1
// Find users who live in city "city".
// Return an array of user_ids. The order does not matter.

function find_user(city, dbname) {
    db = db.getSiblingDB(dbname);
    let results = [];
    let user_ids = db.users.find({"hometown.city": city}, {user_id: 1, _id: 0});
    while (user_ids.hasNext()) {
        let curr_id = user_ids.next();
        results.push(curr_id);
    }
    return results;
}
