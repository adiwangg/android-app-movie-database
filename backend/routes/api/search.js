const express = require("express");
const router = express.Router();
const axios = require("axios");

router.get("/:term", function (req, res) {
  let term = req.params.term;
  let url =
    "https://api.themoviedb.org/3/search/multi?api_key=68e7bb305d9cc82e72833b8ffff4fc66&language=en-US&query=" +
    term;
  axios
    .get(url)
    .then((response) => {
      let temp_res = response.data["results"];
      var results = [];
      var idx = 0;
      for (var i = 0; i < temp_res.length; i++) {
        if (temp_res[i].backdrop_path == null) {
          continue;
        }

        var result = {};
        result.id = temp_res[i].id;
        if (temp_res[i].media_type === "tv") {
          result.title = temp_res[i].name;
          result.year = temp_res[i].first_air_date.split("-")[0];
        } else if (temp_res[i].media_type === "movie") {
          result.title = temp_res[i].title;
          result.year = temp_res[i].release_date.split("-")[0];
        } else {
          continue;
        }

        result.backdrop_path = temp_res[i].backdrop_path;
        result.type = temp_res[i].media_type;
        // if (temp_res[i].vote_average == null) {
          
        // }
        result.rating = (temp_res[i].vote_average / 2).toFixed(1);
        results[idx] = result;
        idx++;
      }

      //console.log(results);
      res.json({ results: results });
      //res.json(response.data["results"]);
    })
    .catch((err) => {
      res.send(err);
    });
});

module.exports = router;
