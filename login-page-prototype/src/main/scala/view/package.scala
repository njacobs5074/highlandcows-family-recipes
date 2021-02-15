import scalatags.Text.all._

package object view {

  val loginFormCssFile: String = "/static/view/css/signin.css"

  def loginForm(errorMessage: Option[String] = None): Frag =
    div(cls := "container")(
      form(cls := "form-signin", action := "/", method := "post")(
        h2("Please sign in"),
        label(`for` := "inputEmail", cls := "sr-only")("Email address"),
        input(
          `type` := "email",
          id := "inputEmail",
          name := "inputEmail",
          cls := "form-control",
          placeholder := "Email address",
          required,
          autofocus
        ),
        label(`for` := "inputPassword", cls := "sr-only")("Password"),
        input(
          `type` := "password",
          id := "inputPassword",
          name := "inputPassword",
          cls := "form-control",
          placeholder := "Password",
          required
        ),
        div(cls := "checkbox")(
          label(input(`type` := "checkbox", value := "remember-me")("Remember me"))
        ),
        for (error <- errorMessage)
          yield i(color.red)(error),
        button(cls := "btn btn-lg btn-primary btn-block", `type` := "submit")("Login")
      )
    )

  def main(user: String): Frag =
    div(cls := "container")(
      h1("Logged In!"),
      p(s"Welcome $user"),
      form(cls := "form-group", action := "/logout", method := "post")(
        button(cls := "btn btn-primary", `type` := "submit")("Logout")
      )
    )
}
