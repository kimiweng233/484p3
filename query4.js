// Query 4
// Find user pairs (A,B) that meet the following constraints:
// i) user A is male and user B is female
// ii) their Year_Of_Birth difference is less than year_diff
// iii) user A and B are not friends
// iv) user A and B are from the same hometown city
// The following is the schema for output pairs:
// [
//      [user_id1, user_id2],
//      [user_id1, user_id3],
//      [user_id4, user_id2],
//      ...
//  ]
// user_id is the field from the users collection. Do not use the _id field in users.
// Return an array of arrays.

function suggest_friends(year_diff, dbname) {
    db = db.getSiblingDB(dbname);

    let pairs = [];
    db.users.aggregate([
        {
            $match: {
                "gender": "male",
            }
        },
        {
            $lookup: {
                from: "users",
                let: { 
                    "city": "$hometown.city", 
                    "state": "$hometown.state", 
                    "country": "$hometown.country", 
                    "yob": "$YOB", 
                    "id": "$user_id", 
                    "maleFriends": "$friends"
                },
                pipeline: [
                    { 
                        $match: {
                            $expr: {
                                $and: [
                                    { $eq: ["$gender", "female"] },
                                    { $eq: ["$hometown.city", "$$city"] },
                                    { $eq: ["$hometown.state", "$$state"] },
                                    { $eq: ["$hometown.country", "$$country"] },
                                    { $lt: [{ $abs: { $subtract: ["$$yob", "$YOB"] } }, year_diff] },
                                    {
                                        $and: [
                                            { $not: { $in: ["$user_id", "$$maleFriends"] }},
                                            { $not: { $in: ["$$id", "$friends"] }}
                                        ]
                                    }
                                ]
                            }
                        }
                    },
                    { $project: { _id: 0, user_id: 1 } }
                ],
                as: "matches"
            }
        },
        { $unwind: "$matches" },
        {
            $project: { _id: 0, user1: "$user_id", user2: "$matches.user_id", }
        },
        { $out: "pairs" }
    ]);

    pairsCursor = db.pairs.find();
    while (pairsCursor.hasNext()) {
        pairs.push(pairsCursor.next());
    }

    return pairs;
}