package api
import cask.router.Decorator

trait ApiRoutes extends cask.Routes {
  override def decorators: Seq[Decorator[_, _, _]] = Seq(new wrapExceptions)
}
