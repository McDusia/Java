//Establish the WebSocket connection and set up event handlers

//CLIENT SIDE
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port+ "/public/");
webSocket.onmessage = function (msg) { paintPage(msg); };
webSocket.onclose = function () { alert("Connection closed") };
var Mode=1;

id("enterWindow").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) {
        var message = String.fromCharCode(1)+e.target.value;
        if (message !== "") {
                    webSocket.send(message); //wysyłamy do servera name i czekamy na listę kanałów od serwera
                    screenMode(2);
        }
    }
});

id("newCanal").addEventListener("click", function () {
    webSocket.send(String.fromCharCode(2));
 });


id("leaveCanal").addEventListener("click", function () {
    screenMode(2);
    webSocket.send(String.fromCharCode(4));
});

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(String.fromCharCode(5)+id("message").value);
});

id("chooseChatbot").addEventListener("click", function () {
    screenMode(4);
    webSocket.send(String.fromCharCode(6));
});

id("leaveChatbot").addEventListener("click", function () {
    screenMode(2);
    webSocket.send(String.fromCharCode(7));
});

id("time").addEventListener("click", function () {
    webSocket.send(String.fromCharCode(6) + "time");
});

id("weekday").addEventListener("click", function () {
    webSocket.send(String.fromCharCode(6) + "weekday");
});

id("weather").addEventListener("click", function () {
    webSocket.send(String.fromCharCode(6) + "weather");
});


function screenMode(mode)
{
    Mode = mode;

    switch(mode){
        case 1:
        id("getName").style.display = '';
        id("canals").style.display = 'none';
        id("canal").style.display = 'none';
        id("chatbot").style.display = 'none';
        break;
        case 2:
        id("getName").style.display = 'none';
        id("canals").style.display = '';
        id("canal").style.display = 'none';
        id("chatbot").style.display = 'none';
        break;
        case 3:
        id("getName").style.display = 'none';
        id("canals").style.display = 'none';
        id("canal").style.display = '';
        id("chatbot").style.display = 'none';
        break;
        case 4:
        id("getName").style.display = 'none';
        id("canals").style.display = 'none';
        id("canal").style.display = 'none';
        id("chatbot").style.display = '';
    }
}

function paintPage(msg) {

    var data = JSON.parse(msg.data);
    var canalList1 = data.canalList;
    var sender = data.sender;
    var message = data.message;

    switch(Mode){
    case 1:
        break;
    case 2:
        displayCanalList(canalList1);
        break;
    case 3:
        updateChat(sender,message);
        break;
    case 4:
        updateChatbot(sender,message);
        break;
    }
}

function getCookieValue(a) {
    var b = document.cookie.match('(^|;)\\s*' + a + '\\s*=\\s*([^;]+)');
    return b ? b.pop() : '';
}

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(sender,msg) {
    if(msg[0]==0){
        msg =msg.substring(1);
        addLeaveCanalInfo(msg);
    }
    else{
        addChatMsg(sender,msg);
    }
}

function updateChatbot(sender,msg) {
    id("responseTime").style.display = 'none';
    id("responseWeekday").style.display = 'none';
    id("responseWeather").style.display = 'none';

    var Id=null;

    switch (sender)
    {
        case "time":
        Id= id("responseTime");
        break;
        case "weekday":
        Id= id("responseWeekday");
        break;
        case "weather":
        Id= id("responseWeather");
        break;
    }
    if (Id!=null)
    {
        Id.style.display = '';
        Id.textContent = msg;
    }
}

function chooseCanal(nr)
{
    webSocket.send(String.fromCharCode(3)+nr);
    id("canalNumber").textContent = "Canal number: "+ nr;
    id("chat").textContent="";
    screenMode(3);
}

function displayCanalList(canalList1)
{
    if(canalList1!="")
        {
            var tab = canalList1.split(",");
            var h="";
            for(i=0;i<tab.length;i++)
            {
                h+="<p align = \"left\"><button align = \"left\" onclick = \"chooseCanal("+tab[i]+")\" id = \"Canal"+tab[i] +"\"> Canal "+tab[i]+" </button></p>";
            }
            id("canalList").innerHTML = h;
        }
}

//Helper functions for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("beforeend", message);
}

function addChatMsg(user, message) {
    var msg = "<span>User "+user+" said:<BR> "+message+"</span><BR><hr>";
    id("chat").insertAdjacentHTML("afterbegin", msg);
}
function addLeaveCanalInfo(message) {
    var msg = "<span>"+message+"</span><BR><hr>";
    id("chat").insertAdjacentHTML("afterbegin", msg);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}
