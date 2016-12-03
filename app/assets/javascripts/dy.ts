// Apologies in advance for the Beatles references
(() => {
  'use strict';
  function obladi(el: Element) {
    let da = el.getAttribute('data-1'),
        la = el.getAttribute('data-2'),
        ob = el.getAttribute('data-3'),
        oblada = ob + la + da;
    return oblada;
    // Life goes on
  }

  let emailSpans = document.querySelectorAll('[data-update="email"]');
  for (let i = 0, len = emailSpans.length; i < len; i++) {
    let element = emailSpans[i],
        address = obladi(element);

    element.innerHTML = address;
  }

  let emailLinks = document.querySelectorAll('[data-update="mailLink"]');
  for (let i = 0, len = emailLinks.length; i < len; i++) {
    let element = emailLinks[i],
        address = obladi(element);

    element.innerHTML = '<a href="mailto:' + address + '">' + address + '</a>';
  }
})();
