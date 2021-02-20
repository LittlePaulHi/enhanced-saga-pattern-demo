package tw.paulchang.core.usecase

import io.reactivex.rxjava3.core.Single

interface UseCase<Request, Response> {
    fun execute(request: Request): Single<Response>
}
