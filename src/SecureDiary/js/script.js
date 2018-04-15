var http = require('http');
var crypto = require('crypto');
var bigInt = require('big-integer');

	var destination = "localhost:8080";
	var KafkaId = "";
	var g = "AC4032EF4F2D9AE39DF30B5C8FFDAC506CDEBE7B89998CAF74866A08CFE4FFE3A6824A4E10B9A6F0DD921F01A70C4AFAAB739D7700C29F52C57DB17C620A8652BE5E9001A8D66AD7C17669101999024AF4D027275AC1348BB8A762D0521BC98AE247150422EA1ED409939D54DA7460CDB5F6C6B250717CBEF180EB34118E98D119529A45D6F834566E3025E316A330EFBB77A86F0C1AB15B051AE3D428C8F8ACB70A8137150B8EEB10E183EDD19963DDD9E263E4770589EF6AA21E7F5F2FF381B539CCE3409D13CD566AFBB48D6C019181E1BCFE94B30269EDFE72FE9B6AA4BD7B5A0F1C71CFFF4C19C418E1F6EC017981BC087F2A7065B384B890D3191F2BFA";
	var N = "AD107E1E9123A9D0D660FAA79559C51FA20D64E5683B9FD1B54B1597B61D0A75E6FA141DF95A56DBAF9A3C407BA1DF15EB3D688A309C180E1DE6B85A1274A0A66D3F8152AD6AC2129037C9EDEFDA4DF8D91E8FEF55B7394B7AD5B7D0B6C12207C9F98D11ED34DBF6C6BA0B2C8BBC27BE6A00E0A0B9C49708B3BF8A317091883681286130BC8985DB1602E714415D9330278273C7DE31EFDC7310F7121FD5A07415987D9ADC0A486DCDF93ACC44328387315D75E198C641A480CD86A1B9E587E8BE60E69CC928B2B9C52172E413042E9B23F10B0E16E79763C9B53DCF4BA80A29E3FB73C16B8E75B97EF363E2FFA31F71CF9DE5384E71B81C0AC4DFFE0C10E64F";
	
	var cache = {
	        name: "",
	        password: ""
	};

	var registerpass = "";
	var solution = "";
	var globalcommand = "";
	
$(function(){
	// Define constants, variables and DOM elements
	var STORAGE_KEY = 'entries_list',
		SEL_CLASS = 'sel';

	var prevTitle = '';
	
	var collapse = true,
		location = null

	var container = $('section.entries'),
		titleField = $('#title'),
		formUsername = $('#formUsername'),
		formPassword = $('#formPassword'),
		contentField = $('#content'),
		status = $('#status'),
		refresh = $('#refresh'),
		remove = $('#remove'),
		register = $('#register'),
		newEntry = $('#newEntry'),
		storeEntry = $('#storeEntry'),
		noEntries = $('#noEntries');

	if (localStorage) {
		// Initialize application
		loadEntries();
		assignEvents();
	} else {
    	alert('This application needs LocalStorage feature and your browser doesn\'t support it.');
	}
	
	// Function definitions

	function deleteEntry() {
		
		var title = $(this).closest('article').children('h2').text();
		titleField.val(title);
		//prevTitle = title; 
		/*
		// Delete entry from list
		var entries = entriesList();
		delete entries[article.attr('rel')];
		article.fadeOut('fast', function() {
			$(this).remove();

			// Save entries
			saveEntries(entries);
		});
		*/
		executeCommand("DELETE");
		// Show entries list
		showList();

		// Reset form
		resetForm();
	}

	function assignEventsToEntries() {
		// Event for deleting an entry
		$("article p img.delete").click(deleteEntry);
		$("article p img.edit").click(function() {
			// Get current entry
			var art = $(this).closest('article');
			
			
			var title = art.children('h2').text();
			titleField.val(title);
			titleField.readOnly = true;
			
			var dirtContent = art.children('p');
			var content = dirtContent.clone().find('span').remove().end().text();
			contentField.val(content);

			// Show form
			showForm('edit');
		});

		// Callback to execute when swipe is completed
		var swipeCallback = function(event) {
			var srcElement = $(event.srcElement) || $(event.target);
			// Add/remove image to delete an entry
			if (srcElement.find('img').length == 0) {
				// Delete swipe-icons when click other elements
				container.find('h2 .swipe-delete').remove();

				srcElement.prepend(function() {
					return $('<span><img class="swipe-delete" src="img/i_delete.png" /></span>').click(deleteEntry);
				});
			} else {
				srcElement.find('img').remove();
			}
		}

		// Callback for clicking in an entry title
		var clickCallback = function(event, target) {
			target = $(target);

			// Check if clicked element is a H2
			if (target.get(0).tagName == 'H2') {
				// Collapse all items except the selected one
				var isSelected = target.parent().hasClass(SEL_CLASS);
				$('article').removeClass(SEL_CLASS);
				if (!isSelected) {
					target.parent().addClass(SEL_CLASS);
				}
			}
				
			if (!target.hasClass('swipe-delete')) {
				// Delete swipe-icons when click other elements
				container.find('h2 .swipe-delete').remove();
			}
		}

		// Capture swipe (left or right)
		var swipeOptions = {
			click:clickCallback,
			swipeLeft:swipeCallback,
			swipeRight:swipeCallback,
			threshold:50
		};
		$("article h2").swipe(swipeOptions);
	}

	function resetForm() {
		// Reset all form fields
		titleField.val('').removeClass('error');
		contentField.val('').removeClass('error');
	}

	/*function submitForm(event) {
		event.preventDefault();

		var type = $(this).attr('rel');

		// Validate input
		var error = false;
		titleField.removeClass('error');
		contentField.removeClass('error');

		// Mark invalid fields
		if (titleField.val().length == 0) {
			titleField.addClass('error');
			error = true;
		}
		if (contentField.val().length == 0) {
			contentField.addClass('error');
			error = true;
		}

		// Show alert and abort if an form values are not valid
		if (error) {
			alert('All fields are mandatory');
			return;
		}

		// Create new JSON entry
		var json_entry = {'title': titleField.val(),
							'content': contentField.val()};

		var entries = entriesList();
		
		// If we are editing, remvoe old element
		if (type != 'new') {
			delete entries[type];
			$('article[rel='+type+']').remove();
		}

		// Use timestamp as key
		var key = Math.round(new Date().getTime() / 1000);

		// Refresh entries
		var entry_html = htmlForEntry(key, json_entry);
		container.prepend(entry_html);
		
		// Save entry
		//entries[key] = json_entry;
		//saveEntries(entries);
	
		// Reassign events
		assignEventsToEntries();

		// Show entries list
		//showList();

		// Reset form
		//resetForm();
	}*/

	function storageChanged(e) {
		loadEntries();
	}

	function entriesChanged() {
		// Show message if there are no entries
		noEntries.remove();
    	/*if (container.html().length == 0) {
    		container.after('<p id="noEntries">No more entries</p>');
    		noEntries = $('#noEntries');
    	}*/
	}

	function assignEvents() {
		// Link to open form
		refresh.unbind('click').click(function() {
			if (cache.name == "")
			{
				authenticateForm("REFRESH");
			}
			else executeCommand("REFRESH");
		});
		
		remove.unbind('click').click(function() {
			if (cache.name == "")
			{
				authenticateForm("REMOVE");
			}
			else executeCommand("REMOVE");
		});
		
		register.unbind('click').click(function() {
				authenticateForm("REGISTER");
		});
		
		$('#btnSubmit').unbind('click').click(function() {
			if (cache.name == "")
			{
				authenticateForm("ADD");
			}
			else 
			{	
				executeCommand("ADD");
				// Show entries list
				showList();
				// Reset form
				resetForm();
			}
		});

		$('h1').click(function() {
			// Expand or collapse all items
			if (collapse) {
				$('article').addClass(SEL_CLASS);
			} else {
				$('article').removeClass(SEL_CLASS);
			}
			collapse = !collapse;
		});

		// Link to open form
		newEntry.unbind('click').click(function() {
			showForm('new');
		});
		
		
		// Link to back to list
		$('#btnCancel').unbind('click').click(function() {
			resetForm();
			showList();
		});

		// Delete swipe-icons when click other elements
		$(document.body).click(function(event) {
			var srcElement =  event.srcElement || event.target;
			if (srcElement.tagName !='H2' && !$(srcElement).hasClass('swipe-delete')) {
				container.find('h2 .swipe-delete').remove();
			}
		});

		// Form submit
		// storeEntry.submit(submitForm);

		// Listener for changes in storage
		//if ('storage' in navigator) {
		//	window.addEventListener('storage', storageChanged, false); 
		//}

	}
	
	function authenticateForm(command) {
		$('#formSubmit').unbind('click').click(function() {
			cache.name = document.getElementById("formUsername").value;
			hash = crypto.createHash('sha256');
			cache.password = bigInt(hash.update(document.getElementById("formPassword").value).digest("hex"), 16);
			
			
			/*predigested_password = document.getElementById("formPassword").value;
			
			var object = {};
	        gen = bigInt(g,16);
	        mod = bigInt(N,16);
	        hash = crypto.createHash('sha256');
	        cache.password = bigInt(hash.update(predigested_password).digest("hex"), 16);
	        */
	        executeCommand(command);
	        
		});
		
		alertify.genericDialog || alertify.dialog('genericDialog',function(){
		    return {
		        main:function(content){
		            this.setContent(content);
		        },
		        setup:function(){
		            return {
		                focus:{
		                    element:function(){
		                        return this.elements.body.querySelector(this.get('selector'));
		                    },
		                    select:true
		                },
		                options:{
		                    basic:true,
		                    maximizable:false,
		                    resizable:false,
		                    padding:false
		                }
		            };
		        },
		        settings:{
		            selector:undefined
		        }
		    };
		});
		//force focusing password box
		alertify.genericDialog ($('#loginForm')[0]).set('selector', 'input[type="text"]');
		
	}
	
	function saveEntries(entries) {
		localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));

		// Process changes
		entriesChanged();
	}

	function showForm(type) {
		// Do the neccessary to show the form
		var comm = "ADD";
		if (type == 'edit') 
		{
			comm = "DELETE";
			prevTitle = titleField.val();
		}
		$('#btnSubmit').unbind('click').click(function() {
			if (cache.name == "")
			{
				authenticateForm(comm);
			}
			else 
			{	
				executeCommand(comm);
				var tit = titleField.val();
				var con = contentField.val();
				// Show entries list
				showList();
				// Reset form
				resetForm();
				titleField.val(tit);
				contentField.val(con);
			}
			
		});
		storeEntry.attr('rel',type);
		container.hide();
		newEntry.hide();
		refresh.hide();
		remove.hide();
		register.hide();
		noEntries.hide();
		storeEntry.fadeIn('fast');
		
		$('#title').on({
			  keydown: function(e) {
				if (e.shiftKey || e.altKey || e.ctrlKey) return false;
			    if (!((e.which > 96 && e.which < 123) || (e.which > 47 && e.which < 58) || (e.which > 64 && e.which < 91) || e.which == 8 || e.which == 37 || e.which == 39) )
			      return false;
			  },
			  change: function() {
			    this.value = this.value.replace(/\s/g, "");
			  }
			});
	}

	function showList() {
		// Do the neccessary to show the entries list
		newEntry.show();
		refresh.show();
		register.show();
		remove.show();
		noEntries.show();		
		storeEntry.hide();
		container.fadeIn('fast');
	}

	function entriesList() {
		var entries_str = localStorage.getItem(STORAGE_KEY);
    	if (!entries_str) {
    		// If there is nothing stored, use an empty object
    		var entries = {};
    		saveEntries(entries);
    		return entries;
    	}
    	return JSON.parse(entries_str);
	}

	function htmlForEntry(key, entry) {
		// HTML for an entry
		var actionLinks = '<span class="actions"><img class="edit" src="img/i_edit.png" /><img class="delete" src="img/i_delete.png" /></span>';
		var date = new Date(key * 1000);

		return '<article rel="'+key+'"><h2>'+entry.title+'</h2><p>'+actionLinks+entry.content+'<span class="time">'+date.toLocaleString()+'</span></p></article>';
	}

	function runEntries(json) {
		container.empty();
		for (var jsonizedEntry in json) {
			var entry = {}
			entry.title = json[jsonizedEntry]["entryname"];
			entry.content = json[jsonizedEntry]["content"];
			var key = Math.round(new Date().getTime() / 1000);
    		container.prepend(htmlForEntry(key, entry));
    	}
		
		assignEventsToEntries();
	}

	function loadEntries() {
   		// Empty renderized content
    	container.empty();

		// If there are entries, show them all!
		var entries = entriesList();
    	for (var key in entries) {
    		container.prepend(htmlForEntry(key, entries[key]));
    	}

		// Process changes
		entriesChanged();

    	// Assign events
    	assignEventsToEntries();
	}
	
	function executeCommand(command)
	{
	    meth = "DEFAULT";
	    url = "/zkauth/";
	    suburluser = "users/";
	    suburldiary = "diary/"
	    deleteurl = titleField.val();
	    if (prevTitle != "") deleteurl = prevTitle;
	    object = {};
	    body = "";
	    heads = {}
	    heads["content-type"] = "application/json";
	    heads["Access-Control-Allow-Origin"] = "*";
	    switch(command)
	    {
			case "REMOVE":
				meth = 'DELETE';
				url += suburluser + cache.name;
			    break;
			case "REGISTER":
				meth = 'POST';
				url += suburluser;
				object.name = cache.name;
				
		        gen = bigInt(g,16);
		        mod = bigInt(N,16);
		        hash = crypto.createHash('sha256');
		        temp = gen.modPow(cache.password, mod);
	            registerpass = dec2hex(temp.toString());
		        
		        object.passwordless = registerpass;
				
				body = JSON.stringify(object);
				heads["content-length"] = body.length;
			    break;
			case "ADD":
				meth = 'POST';
				url += suburldiary;
				object.username = cache.name;
				object.entryname = titleField.val();
				object.content = contentField.val();
				body = JSON.stringify(object);
				heads["content-length"] = body.length;
			    break;
			case "DELETE":
				meth = 'DELETE';
				url += suburldiary + cache.name + "/" + deleteurl;
			    break;
			case "REFRESH":
				meth = 'GET';
				url += suburldiary + cache.name;
			    break;
			default:
			    console.log("Unknown command");
			    return;
		}
	    x = destination.split(':');
	    
	    console.log(meth + " " + url + " " + body);
	    
	    var options = {
			  host: x[0],
			  port: x[1],
			  path: url,
			  method: meth,
			  headers : heads
		};
	    var op = 0;
	    if (command == "REFRESH") op = 1;
	    if (command == "ADD" || command == "REGISTER") op = 2;
	    if (command == "REMOVE") op = 3;
	    if (command == "DELETE") op = 4;
		sendRequestOptions(options,body,op);
	    body = "";
	}

	function sendRequestOptions(options, body, op)
	{
		if (solution != "") options.headers["ZKAuth-Token"] = solution.toString();
		var req = http.request(options, function(r1){
		      status1 = r1.statusCode;
		      r1.on('data', function(chunk){
	      	    response = JSON.parse(chunk);
	      	    if (status1 == 401) 
	      	    {
	      	        challenge = bigInt(response["challenge"], 10);
	      	    	mod = bigInt(N,16);
	      	    	answer = challenge.modPow(cache.password, mod);
	      	    	solution = answer.toString(); 
	      	    	options.headers["ZKAuth-Token"] = solution;
	      	    	console.log("token " + answer.toString())
	      	    	var ret = http.request(options, function(r2){
	      	    	status2 = r2.statusCode;
	      	    	console.log('STATUS #2: ' + status2);
	      	      	  r2.on('data', function(chunk){
	      	      	    if (status2 == 200 || status2 == 201) 
	      	      	    {
	      	      	    	json = JSON.parse(chunk);
	      	      	    	if (op == 1) 
	      	      	    	{
	      	      	    		runEntries(json);
	      	      	    		if (KafkaId == "") spinUpKafkaConsumer(cache.name);
	      	      	    	}
	      	      	    	if (op == 2) 
	      	      	    	{
	      	      	    		executeCommand("REFRESH");
	      	      	    	}
	      	      	    	else alertify.success('Request Success');
	      	      	    	alertify.closeAll();
	      	      	    	document.getElementById("status").src='img/i_online.png';
	      	      	    }
	      	      	    else 
	      	      	    {
	      	      	    	alertify.error('Request Error'); 
	      	      	    	spinDownKafkaConsumer();
	      	      	    	document.getElementById("status").src='img/i_offline.png';
	      	      	    	nullify();
	      	      	    }
	      	      	  });
	      	      	}).on("error", function(e){      	      	  
	      	      	  alertify.error(e.message);
	      	      	  //spinDownKafkaConsumer();
	      	      	  nullify();
	      	      	});
	      	    	if (body != "") ret.write(body);
	      	      	ret.end();
	      	   	}
	      	   	else if (status1 == 201 || status1 == 200) 
	      	   	{
	      	   		json = JSON.parse(chunk);
	      	   		if (op == 1) runEntries(json);
	      	    	if (op == 2) 
	      	    	{
	      	    		executeCommand("REFRESH");
	      	    	}
	      	    	else alertify.success('Request Success');
	      	   		alertify.closeAll();
	      	   		document.getElementById("status").src='img/i_online.png';
	      	   	}
	      	   	else
	      	   	{
	      	   		alertify.error(response["message"]);
	      	   		nullify();
	      	   	}
	      	  });
		      if (status1 == 200 && (op != 1)) 
		      {
		    	 if (op == 2) executeCommand("REFRESH");
		    	 if (op == 3)
		    	 {
	      	    	 alertify.warning('User removed: ' + cache.name); 
	      	    	 spinDownKafkaConsumer();
	      	    	 document.getElementById("status").src='img/i_offline.png';
	      	    	 nullify();
	      	    	 container.empty();
		    	 }
		    	 if (op == 4) 
		    	 {
		    		 if (prevTitle != "")
		    		 {
		    			 executeCommand("ADD");
		    			 prevTitle = "";
		    		 }
		    		 else executeCommand("REFRESH");
		    	 }
		      }	
	      	}).on("error", function(e){
	      	  alertify.error(e.message);
	      	  nullify();
	      	});
	      	
		if (body != "") req.write(body);
	  	req.end();
	}
	
	function nullify()
	{
		cache.name = "";
		cache.password = "";
		document.getElementById("formPassword").value = '';
		document.getElementById("formUsername").value = '';
	}

	function dec2hex(str){ 
	    var dec = str.toString().split(''), sum = [], hex = [], i, s
	    while(dec.length){
	        s = 1 * dec.shift()
	        for(i = 0; s || i < sum.length; i++){
	            s += (sum[i] || 0) * 10
	            sum[i] = s % 16
	            s = (s - sum[i]) / 16
	        }
	    }
	    while(sum.length){
	        hex.push(sum.pop().toString(16))
	    }
	    return hex.join('')
	}

	function spinUpKafkaConsumer(topic)
	{
		var http = new XMLHttpRequest();
		var url = "http://10.8.120.53:8082/consumers/my_json_consumer";
		var params = '{"name": "my_consumer_instance", "format": "json", "auto.offset.reset": "smallest"}';
		
		http.open("POST", url, true);

		//Send the proper header information along with the request
		http.setRequestHeader("Content-type", "application/vnd.kafka.json.v1+json");

		http.onreadystatechange = function() {//Call a function when the state changes.
			if(http.readyState == 4 && http.status == 200) {
				KafkaId = setInterval(function() {
					var http2 = new XMLHttpRequest();
					var url2 = "http://10.8.120.53:8082/consumers/my_json_consumer/instances/my_consumer_instance/topics/"+topic;
					
					http2.open("GET", url2, true);
		
					//Send the proper header information along with the request
					http2.setRequestHeader("Accept", "application/vnd.kafka.json.v1+json");
		
					http2.onreadystatechange = function() {//Call a function when the state changes.
					    if(http2.readyState == 4 && http2.status == 200) {
					        json = JSON.parse(http2.responseText);
					        
					        for (var obj in json) {
					        	chal = bigInt(json[obj]["value"]["challenge"],16);
					            mod = bigInt(N,16);
					            solution = chal.modPow(cache.password, mod).toString();
					            console.log("Set solution to " + solution);
					        }
					        
					    }
					}
					http2.send(params);
				}, 3000);
		    }
		}
		http.send(params);
	}

	function spinDownKafkaConsumer()
	{
		clearInterval(KafkaId);
		KafkaId = "";
		var http = new XMLHttpRequest();
		var url = "http://10.8.120.53:8082/consumers/my_json_consumer/instances/my_consumer_instance";
		//Send the proper header information along with the request
		
		http.open("DELETE", url, true);
		http.setRequestHeader("Accept", "application/vnd.kafka.json.v1+json");
		
		http.onreadystatechange = function() {//Call a function when the state changes.
		    //alert("DELETE " + http.responseText)
			if(http.readyState == 4 && http.status == 200) {
		        alert("Deleted from Kafka " + http.responseText);
		    }
		}
		
		http.send(null);
	}

});