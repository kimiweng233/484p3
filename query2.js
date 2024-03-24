function unwind_friends(dbname) {
    db = db.getSiblingDB(dbname);
    db.users.aggregate([
        { $unwind: "$friends" },
        { $project: {
                _id: 0,
                user_id: 1,
                friends: "$friends"
            }
        },
        { $out: "flat_users" }
    ])
    return;
}
