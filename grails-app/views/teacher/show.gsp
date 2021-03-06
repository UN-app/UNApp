<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<div ng-app="CommentsApp" class="container subjectInfo">
    <div class="row">
        <div class="col-xs-12">
            <div class="jumbotron">
                <div class="row">
                    <div class="col-md-4 col-lg-5">
                        <img src="${assetPath(src: 'teacher.png')}" class="img-rounded center-block photo-size"
                             height="250"
                             width="250">

                        <h2 class="text-center">${result.name}</h2>

                        <h3 class="text-center">${result.email}</h3>

                        <h4 class="text-center">${result.location}</h4>

                        <div class="row text-center">
                            <g:render template="starRating"/>
                        </div>

                        <div class="col-xs-12 display-rating" style="text-align: center">Tu voto fue de:</div>

                    </div>

                    <div class="col-md-8 col-lg-7">
                        <h2>Información:</h2>

                        <div class="text-justify" style="white-space: pre-line; word-wrap: break-word">
                            ${result.information}
                        </div>

                        <h2>Links:</h2>

                        <div class="text-justify"><a href="${result.URL}">Pagina docente</a></div>

                        <h2>Cursos:</h2>
                        <g:each in="${result.courses}" status="i" var="course">
                            <a href="${request.contextPath}/course/show?id=${course.id}">${course.name}</a>
                            <br/>
                        </g:each>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12">
            <div class="jumbotron">
                <oauth:disconnected provider="google">
                    <div class="row">
                        <div class="col-xs-12">
                            <div class="text-center">
                                <oauth:connect provider="google"
                                               redirectUrl="${request.forwardURI.replace("/UNApp", "") + ((request.queryString) ? "?" + request.queryString : "")}">
                                    <button class="btn btn-raised btn-primary">
                                        <i class="fa fa-google"></i> Ingresar con Google Para Comentar
                                    </button>
                                </oauth:connect>
                            </div>
                        </div>
                    </div>
                </oauth:disconnected>
                <oauth:connected provider="google">
                    <g:render template="/comment/commentForm"/>
                </oauth:connected>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-4">
            <div class="jumbotron">
                <h2 class="text-right">Comentarios:</h2>
                <oauth:connected provider="google">
                    <g:render template="/comment/advanceSearch"/>
                </oauth:connected>
            </div>
            <unapp:isAdmin>
                <div class="jumbotron">
                    <h2 class="text-center"><i class="fa fa-cogs"></i> Administrar</h2>
                    <div class="row text-center">
                        <div class="col-xs-12 col-sm-6 col-md-12">
                            <button class="btn btn-deafult btn-raised" type="button" onclick="window.location.assign('${request.contextPath}/teacher/edit?id=${result.id}')">
                                <i class="fa fa-pencil-square-o"></i> Editar
                            </button>
                        </div>
                        <div class="col-xs-12 col-sm-6 col-md-12">
                            <button class="btn btn-deafult btn-raised" type="button" onclick="window.location.assign('${request.contextPath}/teacher/edit?id=${result.id}')">
                                <i class="fa fa-eraser"></i> Borrar
                            </button>
                        </div>
                    </div>
                </div>
            </unapp:isAdmin>
        </div>

        <div class="col-md-8">
            <g:render template="/comment/comments"/>
        </div>
    </div>
</div>
</body>
</html>