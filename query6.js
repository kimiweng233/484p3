// Query 6
// Find the average friend count per user.
// Return a decimal value as the average user friend count of all users in the users collection.

function find_average_friendcount(dbname) {
  db = db.getSiblingDB(dbname);

  var ans = db.users
    .aggregate([
      {
        $group: { _id: null, avg_friends: { $avg: { $size: "$friends" } } },
      },
    ])
    .toArray();
  return ans[0].avg_friends;
}
