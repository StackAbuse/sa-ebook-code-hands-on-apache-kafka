        $(document).ready(function() {

            var url = 'http://localhost:8080/sse/newsfeed';
            var eventSource = new EventSource(url);

            eventSource.onopen = function () {
               console.log('connection is established');
            };

            eventSource.onmessage = function (event) {
                console.log('id: ' + event.lastEventId + ', data: ' + event.data);
                var newsData = JSON.parse( event.data );
                console.log('Event: ', event);
                addBlock(newsData.title, newsData.text);
                if (event.data.endsWith('.')) {
                   eventSource.close();
                   console.log('connection is closed');
                }
            };

            eventSource.onerror = function (event) {
                console.log('connection state: ' + eventSource.readyState + ', error: ' + event);
            };

            window.onbeforeunload = function() {
                eventSource.close();
            };

            function addBlock(title, text) {
                console.log('title: ', title);
                console.log('text: ', text);
                var a = document.createElement('article');

                // title
                var h = document.createElement('h4');
                var t = document.createTextNode(title);
                h.appendChild(t);

                // paragraph
                var para = document.createElement('P');
                para.innerHTML = text;

                a.appendChild(h);
                a.appendChild(para);
                document.getElementById('pack').appendChild(a);
            }
        })