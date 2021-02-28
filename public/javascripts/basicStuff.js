$('#add-player-submit').click(function(evt) {
    const name = $("#player-name").val();
    console.log("name:", name)
    const word = $("#secret-word").val();
    console.log("word:", word)
    const url = "apiAddPlayer";
    console.log("url:", url);

	$.post(url, { playerName: name, secretWord: word }, data => {
	    $("#page").html(data);
	});
});

$('#guess-submit').click(function(evt) {
    const name = $("#guess").val();
    const url = "apiAddPlayer";
    console.log("url:", url);

	$.post(url, { playerName: name, secretWord: word }, data => {
	    $("#page").html(data);
	});
});

//$('#clickMe').click(function(evt) {
//    const squareMe = $("#squareMe").val();
//    console.log("squareMe:", squareMe)
//    const url = "squarePost";
//    console.log("url:", url);
//    const n = squareMe;
//
//	$.post(url, { n }, data => {
//	    $("#square").html(data);
//	});
//});

//    const squareMe = $("#squareMe").val();
//    console.log("squareMe:", squareMe)
//    const url = "/squarePost/";
//    console.log("url:", url);
//
//	$.post("/squarePost", squareMe, data => {
//	    $("#square").html(data);
//	});

//clickMe.onclick = () => {
//    const squareMe = document.getElementById("squareMe");
//    const url = "/square/" + squareMe.value;
//    console.log("url:", url)
//
//    fetch(url).then((responsePromise) => {
//        return responsePromise.text();
//    }).then((responseText) => {
//        document.getElementById("square").innerHTML = responseText;
//    });
//};

//      console.log('clicked!');
//	  var inputString = $('#squareMe').val();
//
//      var obj = {
//        n: inputString
//      };
//
//      console.log('obj:', obj);
//
//      $.ajax({
//        url: '/squarePost',
//        data: JSON.stringify(obj),
//        headers: {
//          'Content-Type': 'application/json'
//        },
//        type: 'POST',
//        success: function(res) {
//          if (res) {
//            console.log("Success!");
//          } else {
//            console.log("Failed...");
//          }
//        }
//      });
//
//      console.log('done!');
