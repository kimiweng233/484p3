// Query 5
// Find the oldest friend for each user who has a friend. For simplicity,
// use only year of birth to determine age, if there is a tie, use the
// one with smallest user_id. You may find query 2 and query 3 helpful.
// You can create selections if you want. Do not modify users collection.
// Return a javascript object : key is the user_id and the value is the oldest_friend id.
// You should return something like this (order does not matter):
// {user1:userx1, user2:userx2, user3:userx3,...}

function oldest_friend(dbname) {
  db = db.getSiblingDB(dbname);

  let results = {};

  db.users.aggregate([
    { $unwind: "$friends" },
    {
      $project: {
        _id: 0,
        user_id: 1,
        friends: "$friends",
        YOB: 1,
      },
    },
    { $out: "flat_users" },
  ]);

  db.flat_users.find().forEach(function (myDoc) {
    let yob = db.users.findOne({ user_id: myDoc.friends }, { YOB: 1 }).YOB;
    db.flat_users.insert({
      user_id: myDoc.friends,
      friends: myDoc.user_id,
      YOB: yob,
    });
  });

  // db.flat_users.find().forEach(function (myDoc) {
  //   let yob = db.users
  //     .find({ user_id: myDoc.friends }, { YOB: 1 })
  //     .toArray()[0].YOB;

  //   db.flat_users.updateOne(
  //     {
  //       user_id: myDoc.user_id,
  //       friends: myDoc.friends,
  //     },
  //     { $set: { YOB: yob } }
  //   );
  // });

  db.flat_users.aggregate([
    { $sort: { YOB: 1, user_id: 1 } },
    { $group: { _id: "$friends", friends: { $first: "$user_id" } } },
    { $out: "oldest_friend" },
  ]);

  db.oldest_friend.find().forEach(function (myDoc) {
    results[myDoc._id] = myDoc.friends;
  });

  // const set = new Set();
  // db.flat_users
  //   .find()
  //   .sort({ YOB: 1, user_id: 1 })
  //   .forEach(function (myDoc) {
  //     if (!set.has(myDoc.user_id)) {
  //       set.add(myDoc.user_id);
  //       results[myDoc.user_id] = myDoc.friends;
  //     }
  //   });

  return results;
}
