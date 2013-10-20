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
        beforeSend: gcmt.preSubmitPost()
      })
      .done(function(html) {
        gcmt.postSuccess(0, html)
      })
      .fail(function( jqXHR, textStatus, error ) {
        alert( "Request failed: " + textStatus + ": " + error );
      })
    });

  };

// Submit the blog post
  gcmt.preSubmitPost = function(){
    $("#gcmtMainWorkspace, #gcmtMainSidebarNav").queue(function() {
      $(this).addClass("gcmtMainWorkspaceWait")
      $(this).delay(250);
      $(this).dequeue();
    });
  }

  gcmt.postSuccess = function(navPage, html) {
    console.log(html);
    $(".gcmtMainWorkspaceContainer").queue( function() {
      $(".gcmtMainWorkspace").addClass("gcmtUiDestroy");
      $(this).delay(500);
      $(this).empty();
      $(this).append(html);
      $(".gcmtMainWorkspace").addClass("gcmtUiInit");
      $(this).dequeue
      gcmt.enableBlogpostSubmit(postId);
    });

    $("#gcmtMainSidebarNav").queue(function(){
      $(this).unload();
      gcmt.loadNavBar(navPage);
      $(this).dequeue();
    });
  }

// Load Nav Bar
  gcmt.loadNavBar = function(page) {
    $("#gcmtMainSidebarNav").load("/manage/blogPosts/list/" + page, function(response, status, xhr) {
      if (status == "error") {
        var msg = "Oops, there was an error: ";
        alert(msg + xhr.status + " " + xhr.statusText)
      } else {
        gcmt.enableLeftNav("blogPosts");
      }
    });
  };

// Make post listings clickable
  gcmt.enableLeftNav = function(mode) {
    $('#gcmtMainSidebarNav').children().click(function() {
      var contentId = $(this).attr('data-content-id');

      $(".gcmtMainWorkspaceContainer").queue( function() {
        $(".gcmtMainWorkspace").addClass("gcmtUiDestroy");
        $(this).delay(500);
        $(this).empty();
        $(this).delay(1400).load('/manage/' + mode + '/edit/' + contentId, function(response, status, xhr) {
          if (status == "success") {
            $(".gcmtMainWorkspace").addClass("gcmtUiInit");
          };
        });
        gcmt.enableBlogpostSubmit(postId);
        $(this).dequeue();
      });
    });
  };




// Load Pages into Nav Bar
  gcmt.loadPagesNav = function(page) {
    $("#gcmtMainSidebarNav").load("/manage/pages/list", function(response, status, xhr) {
      if (status == "error") {
        var msg = "Oops, there was an error: ";
        alert(msg + xhr.status + " " + xhr.statusText)
      } else {
        gcmt.enableLeftNav("pages");
      }
    });
  };


