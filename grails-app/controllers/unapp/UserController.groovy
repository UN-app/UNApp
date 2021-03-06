package unapp



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class UserController {

    static allowedMethods = [save: "POST", show: "GET" , update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond User.list(params), model:[userInstanceCount: User.count()]
    }

    def show(int id) {
        def result = User.get(id).collect { user ->
            [id         : user.id,
             name       : user.name,
             googleID   : user.googleID,
             email      : user.email,
             picture    : user.picture,
             positiveVotes     : user.positiveVotes,
             negativeVotes     :user.negativeVotes,
            ]
        }[0]

        respond result, model: [result: result]
    }

    def search(String query) {
        def result = User.findAllByNameIlike("%" + query + "%").collect { c ->
            [id: c.id, name: c.name, picture: c.picture]
        }

        respond result, model: [result: result]
    }

    def create() {
        respond new User(params)
    }

    def comments(int id, int max, int offset) {
        def result = Comment.findAllByAuthor(User.get(id), [sort: "date", order: "desc", max: max, offset: offset]).collect { comment ->
            [id           : comment.id,
             author       : [id:comment.author?.id, name:  comment.author?.name],
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

    @Transactional
    def save(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view:'create'
            return
        }

        userInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
                redirect userInstance
            }
            '*' { respond userInstance, [status: CREATED] }
        }
    }

    def edit(User userInstance) {
        respond userInstance
    }

    @Transactional
    def update(User userInstance) {
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view:'edit'
            return
        }

        userInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect userInstance
            }
            '*'{ respond userInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(User userInstance) {

        if (userInstance == null) {
            notFound()
            return
        }

        userInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
