// * Modify the users and tweets collections so that they have a meta field
// * containing a real date type version of the created_at key.

// Introduce the meta subdocument to users table.

db["twitter_users"].find({"meta": null}).forEach(
  function(doc){
    doc.meta = {"created_at": new Date(doc.created_at)};
    db["twitter_users"].save(doc);
  }
); 

db["twitter_users"].ensureIndex({"id": 1});

// Introduce the meta subdocument to tweets table.

db["twitter"].find({"user.meta": null}).forEach(
  function(doc){
    var date = new Date(doc.created_at);
    var offset = doc.user.utc_offset;
    if (offset == null) {
      var local_date = date;
    } else {
      var local_date = new Date(date).setSeconds(date.getSeconds() + offset);
    }
    doc.meta = {"created_at": date, "created_at_local": local_date};
    doc.user.meta = {"created_at": new Date(doc.user.created_at)};
    db["twitter"].save(doc);
  }
); 

db["twitter"].ensureIndex({"id": 1});
db["twitter"].ensureIndex({"meta.created_at": -1});
