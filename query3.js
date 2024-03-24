function cities_table(dbname) {
    db = db.getSiblingDB(dbname);
    db.users.aggregate([
        {
            $group: {
                _id: "$current.city",
                users: { $addToSet: "$user_id" }
            }
        },
        { $out: "cities" }
    ])
    return;
}
