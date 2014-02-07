console.log("JavaScript Running!");

var element = document.getElementById("js-test");
if(element){
	element.textContent = "I executed external JavaScript!";
}

$('#js-test').css("color", "#333333");