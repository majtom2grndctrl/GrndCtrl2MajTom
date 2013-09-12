$(function(){

// Hide JS required warnings
  $('#gcmt-JsRequired').hide(0);

  var gcmt = {};
  gcmt.blogPage = 0;
  gcmt.blogPostId;

// Load Blogposts JSON
  function loadBlogpostsJson (page) {
    $.getJSON( "/manage/blogPosts/list/"+page)
    .done( function(blogposts) {
      consol.log(blogposts);
    	/*      $.each( blogposts.items, function ( i, item ) {
        $( "<li/>")
      });*/
    });
  }
// Load Nav Bar
  function loadNavBar(page){
	  
    $(".gcmtMainSidebarList").load("/manage/blogPosts/list/" + page, function(response, status, xhr) {
      if (status == "error") {
        var msg = "Oops, there was an error: ";
        alert(msg + xhr.status + " " + xhr.statusText)
      }
// Make post listings clickable
      $('.gcmtMainSidebarList').children().click(function() {
        gcmt.postId = $(this).attr('data-post-id');
        $(".gcmtMainWorkspace").addClass("gcmtUiDestroy");

        $(".gcmtMainWorkspaceContainer").queue( function() {
          $(this).empty();
          $(this).delay(1400).load('/manage/blogPosts/' + gcmt.postId, function(response, status, xhr){
            if (status == "success") {
              $(".gcmtMainWorkspace").addClass("gcmtUiInit");
        	};
          });
          $(this).dequeue();
        });
      });

    });
    if(page != null) {
      blogPage = page;
    } else {
      blogPage = 1;
    }
  };
  
// Submit the blog post

  function submitPost(){
    $("#gcmtMainWorkspace, .gcmtMainSidebarList").queue(function() {
      $(this).addClass("gcmtMainWorkspaceWait").delay(1000).dequeue();
    });
  }

  function postSuccess() {
    $(".gcmtMainSidebarList").queue(function(){
      $(this).unload();
      $(this).load("/manage/blogPosts/list/0");
      $(this).dequeue();
  	alert("Submit");
    });
  };

  $("#gcmtMainWorkspace").submit(function(){
    var dataObject = new FormData(document.getElementById("gcmtMainWorkspace"));
    var localString;
    if(blogPostId == null) {
    	localString = "new";
    } else {
    	localString = blogPostId;
    };
    $.ajax({
      url: "/manage/blogPosts/" + localString,
      type: "POST",
      data: dataObject,
      processData: false,
      contentType: false,
      beforeSend: submitPost(),
      success: postSuccess()
    });
    loadNavBar(1);
    return false;
  });


// Initialize the page
  loadNavBar(0);
  loadBlogpostsJson(0);
  $("#gcmtMainWorkspace").queue(function(){
    $(this).delay(500);
    $(this).addClass("gcmtUiInit");
    $(this).dequeue();
  });
});
