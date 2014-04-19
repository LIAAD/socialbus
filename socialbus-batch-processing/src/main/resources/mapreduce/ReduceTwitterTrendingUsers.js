function(key, values) {
var count = 0;
  values.forEach(function(v) {
    count += v['count'];
  });
  return {count: count};
}