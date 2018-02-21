$(function(){
	// Define constants, variables and DOM elements
	var STORAGE_KEY = 'entries_list',
		SEL_CLASS = 'sel';

	var collapse = true,
		location = null

	var container = $('section.entries'),
		titleField = $('#title'),
		contentField = $('#content'),
		onlineStatus = $('#onlineStatus'),
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
		var article = $(this).closest('article');

		// Delete entry from list
		var entries = entriesList();
		delete entries[article.attr('rel')];
		article.fadeOut('fast', function() {
			$(this).remove();

			// Save entries
			saveEntries(entries);
		});
	}

	function assignEventsToEntries() {
		// Event for deleting an entry
		$("article p img.delete").click(deleteEntry);
		$("article p img.edit").click(function() {
			// Get current entry
			var entries = entriesList();
			var key = $(this).closest('article').attr('rel');
			var entry = entries[key];

			// Populate form
			titleField.val(entry.title);
			contentField.val(entry.content);

			// Show form
			showForm(key);
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

	function submitForm(event) {
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
							'content': contentField.val(),
							'location': location};

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
		entries[key] = json_entry;
		saveEntries(entries);

		// Reassign events
		assignEventsToEntries();

		// Show entries list
		showList();

		// Reset form
		resetForm();
	}

	function storageChanged(e) {
		loadEntries();
	}

	function entriesChanged() {
		// Show message if there are no entries
		noEntries.remove();
    	if (container.html().length == 0) {
    		container.after('<p id="noEntries">You have no entries yet</p>');
    		noEntries = $('#noEntries');
    	}
	}

	function navigatorOn() {
		onlineStatus.hide();
	}

	function navigatorOff() {
		onlineStatus.show();
	}

	function assignEvents() {
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
		newEntry.click(function() {
			showForm('new');
		});

		// Link to back to list
		$('#btnCancel').click(function() {
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
		storeEntry.submit(submitForm);

		// Listen to online status changes
		if ('onLine' in navigator) {
			if (navigator.onLine) {
				navigatorOn();
			} else {
				navigatorOff();    
			}

			window.addEventListener('online', navigatorOn);
			window.addEventListener('offline', navigatorOff);
		}

		// Listener for changes in storage
		if ('storage' in navigator) {
			window.addEventListener('storage', storageChanged, false); 
		}

	}
	
	function saveEntries(entries) {
		localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));

		// Process changes
		entriesChanged();
	}

	function showForm(type) {
		// Do the neccessary to show the form
		storeEntry.attr('rel',type);
		container.hide();
		newEntry.hide();
		noEntries.hide();
		storeEntry.fadeIn('fast');
	}

	function showList() {
		// Do the neccessary to show the entries list
		newEntry.show();
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

		return '<article rel="'+key+'"><h2>'+entry.title+'</h2><p>'+actionLinks+entry.content+'<span class="time">'+date.toLocaleString()+'</span>'+location+'</p></article>';
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
});