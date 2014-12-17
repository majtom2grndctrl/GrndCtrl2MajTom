// Revealing Prototype Dropdown Menu, depends on jQuery
var rpdm = function(categoriesButton, categoriesMenu, fadeInLength, fadeOutLength) {
    this.categoriesButton = categoriesButton;
    this.categoriesMenu = categoriesMenu;
    this.fadeInLength = fadeInLength || 150;
    this.fadeOutLength = fadeOutLength || 250;
};
rpdm.prototype = function() {

    var buttonClick = function() {
            var args = this;
            this.categoriesButton.click( function(event) {
                args.categoriesMenu.fadeIn(args.fadeInLength);
                event.stopPropagation();
            });
            return this;
        },

        documentClick = function() {
            var args = this;
            $(document).click( function() {
                args.categoriesMenu.fadeOut(args.fadeOutLength);
            });
            return this;
        };
    return {
        buttonClick: buttonClick,
        documentClick: documentClick
    };
} ();

