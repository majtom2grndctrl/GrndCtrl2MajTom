/* rpdm.js, Copyright 2014 Dan Hiester. License:

 The MIT License (MIT)

 Copyright (c) 2014 Dan Hiester

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

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

