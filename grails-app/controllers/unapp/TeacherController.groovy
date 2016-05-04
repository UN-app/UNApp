package unapp


import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.converters.JSON

@Transactional(readOnly = true)
class TeacherController {

    static allowedMethods = [index: "GET", show: "GET", comments: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        def result = Location.list().collect { l ->
            [name    : l.name,
             teachers: Teacher.findAllByLocation(l, [sort: "name", order: "asc"]).collect { c ->
                 [id: c.id, name: c.name, email: c.getEmail()]
             }
            ]
        }

        respond result, model: [result: result]
    }

    def search(String query) {
        def result = Teacher.findAllByNameIlike("%" + query + "%").collect { c ->
            [id: c.id, name: c.name, email: c.getEmail()]
        }

        respond result, model: [result: result]
    }

    def show(int id) {
        def result = Teacher.get(id).collect { teacher ->
            [id         : teacher.id,
             name       : teacher.name,
             email      : teacher.getEmail(),
             information: teacher.information,
             location   : teacher.location.name,
             URL        : teacher.getURL(),
             courses    : teacher.courses.collect { course ->
                 [id  : course.id,
                  name: course.name,
                  code: course.code
                 ]
             }
            ]
        }[0]

        respond result, model: [result: result]
    }

    def erase(){
        render template: "erase"
    }

    def comments(int id, int max, int offset) {
        def result = Comment.findAllByTeacher(Teacher.get(id), [sort: "date", order: "desc", max: max, offset: offset]).collect { comment ->
            [id           : comment.id,
             author       : comment.author.name,
             picture      : comment.author.picture,
             body         : comment.body,
             date         : comment.date.format("yyyy-MM-dd 'a las' HH:mm"),
             voted        : Vote.findByAuthorAndComment(session.user, comment)?.value ?: 0,
             positiveVotes: comment.countPositiveVotes(),
             negativeVotes: comment.countNegativeVotes(),
             course       : [id: comment.course?.id, name: comment.course?.name],
             teacher      : [id: comment.teacher?.id, name: comment.teacher?.name]
            ]
        }

        respond result, model: [result: result]
    }

    def create() {
        respond new Teacher(params)
    }

    @Transactional
    def save(Teacher teacherInstance) {
        if (teacherInstance == null) {
            notFound()
            return
        }

        if (teacherInstance.hasErrors()) {
            respond teacherInstance.errors, view: 'create'
            return
        }

        teacherInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'teacher.label', default: 'Teacher'), teacherInstance.id])
                redirect teacherInstance
            }
            '*' { respond teacherInstance, [status: CREATED] }
        }
    }

    @Transactional
    def deleteAux(){
        def id = request.JSON.id
        def teacher = Teacher.findById(id)
        try{
            teacher.delete(failOnError:true, flush:true)
            render "true"
        }
        catch(e) {
            render "false"
        }
    }


    def edit(Teacher teacherInstance) {
        respond teacherInstance
    }

    @Transactional
    def update(Teacher teacherInstance) {
        if (teacherInstance == null) {
            notFound()
            return
        }

        if (teacherInstance.hasErrors()) {
            respond teacherInstance.errors, view: 'edit'
            return
        }

        teacherInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Teacher.label', default: 'Teacher'), teacherInstance.id])
                redirect teacherInstance
            }
            '*' { respond teacherInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Teacher teacherInstance) {

        if (teacherInstance == null) {
            notFound()
            return
        }

        teacherInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Teacher.label', default: 'Teacher'), teacherInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'teacher.label', default: 'Teacher'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}
