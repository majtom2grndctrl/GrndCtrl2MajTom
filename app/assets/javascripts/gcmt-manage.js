$(function(){

// Hide JS required warnings
  $('#gcmt-JsRequired').hide(0);

  var gcmt = {};
  gcmt.manage = {};

  var enableBlogpostSubmit = function(postId) {
    $("#gcmtMainWorkspace").submit(function(event){
      event.preventDefault();
      alert("Submit intercepted");
      var dataObject = new FormData(document.getElementById("gcmtMainWorkspace"));
      var urlString;
      if ( postId === 0 ) {
        urlString = "new";
      } else {
        urlString = postId;
      }
      var localString;
      $.ajax({
        url: "/manage/blogPosts/" + urlString,
        type: "POST",
        data: dataObject,
        processData: false,
        contentType: false,
        beforeSend: preSubmitPost(),
        success: postSuccess(postId)
      });
      return false;
    });
  };

// Make post listings clickable
  var enableLeftNav = function() {
    $('#gcmtMainSidebarNav').children().click(function() {
      var postId = $(this).attr('data-post-id');
      $(".gcmtMainWorkspace").addClass("gcmtUiDestroy");

      $(".gcmtMainWorkspaceContainer").queue( function() {
        $(this).empty();
        $(this).delay(1400).load('/manage/blogPosts/' + postId, function(response, status, xhr){
          if (status == "success") {
            $(".gcmtMainWorkspace").addClass("gcmtUiInit");
          };
        });
        enableBlogpostSubmit(postId);
        $(this).dequeue();
      });
    });
  };

// Load Nav Bar
  var loadNavBar = function(page) {
    $("#gcmtMainSidebarNav").load("/manage/blogPosts/list/" + page, function(response, status, xhr) {
      if (status == "error") {
        var msg = "Oops, there was an error: ";
        alert(msg + xhr.status + " " + xhr.statusText)
      }
    });
    enableLeftNav();
  }

// Submit the blog post
  var preSubmitPost = function(){
    $("#gcmtMainWorkspace, #gcmtMainSidebarNav").queue(function() {
      $(this).addClass("gcmtMainWorkspaceWait").delay(1000).dequeue();
    });
  }

  var postSuccess = function(id) {
    $("#gcmtMainSidebarNav").queue(function(){
      $(this).unload();
      $(this).load("/manage/blogPosts/list/" + id);
      $(this).dequeue();
    });
  };

// Initialize the page
  loadNavBar(0);
  enableBlogpostSubmit(0);
//  loadBlogpostsJson(0);
  $("#gcmtMainWorkspace").queue(function(){
    $(this).delay(500);
    $(this).addClass("gcmtUiInit");
    $(this).dequeue();
  });
});
