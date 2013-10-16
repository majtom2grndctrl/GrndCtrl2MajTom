  var gcmt = {};

  gcmt.enableBlogpostSubmit = function( postId ) {
    $("#gcmtMainWorkspace").submit( function( event ) {
      event.preventDefault();
      var dataObject = new FormData(document.getElementById("gcmtMainWorkspace"));
      $.ajax({
        url: "/manage/blogPosts/" + postId,
        type: "POST",
        data: dataObject,
        processData: false,
        contentType: false,
        beforeSend: gcmt.preSubmitPost(),
        success: gcmt.postSuccess(0)
      })
      .fail(function( jqXHR, textStatus ) {
        alert( "Request failed: " + textStatus );
      });
      return false;
    });
  };

// Load Nav Bar
  gcmt.loadNavBar = function(page) {
    $("#gcmtMainSidebarNav").load("/manage/blogPosts/list/" + page, function(response, status, xhr) {
      if (status == "error") {
        var msg = "Oops, there was an error: ";
        alert(msg + xhr.status + " " + xhr.statusText)
      } else {
        gcmt.enableLeftNav();
      }
    });
  };

// Make post listings clickable
  gcmt.enableLeftNav = function() {
    $('#gcmtMainSidebarNav').children().click(function() {
      var postId = $(this).attr('data-post-id');

      $(".gcmtMainWorkspaceContainer").queue( function() {
        $(".gcmtMainWorkspace").addClass("gcmtUiDestroy");
        $(this).delay(500);
        $(this).empty();
        $(this).delay(1400).load('/manage/blogPosts/' + postId, function(response, status, xhr) {
          if (status == "success") {
            $(".gcmtMainWorkspace").addClass("gcmtUiInit");
          };
        });
        gcmt.enableBlogpostSubmit(postId);
        $(this).dequeue();
      });
    });
  };

// Submit the blog post
  gcmt.preSubmitPost = function(){
    $("#gcmtMainWorkspace, #gcmtMainSidebarNav").queue(function() {
      $(this).addClass("gcmtMainWorkspaceWait")
      $(this).delay(1000)
      $(this).dequeue();
    });
  }

  gcmt.postSuccess = function(page) {
    $("#gcmtMainSidebarNav").queue(function(){
      $(this).unload();
      $(this).load("/manage/blogPosts/list/" + page);
      $(this).dequeue();
    });
  };
