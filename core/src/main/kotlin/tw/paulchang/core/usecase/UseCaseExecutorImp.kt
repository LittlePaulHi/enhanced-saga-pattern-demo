package tw.paulchang.core.usecase

import io.reactivex.rxjava3.core.Single

class UseCaseExecutorImp : UseCaseExecutor {
    override fun <Request, Response> invoke(
        useCase: UseCase<Request, Response>,
        request: Request
    ): Single<Response> {
        return useCase.execute(request)
    }

    override operator fun <RequestDto, ResponseDto, Request, Response> invoke(
        useCase: UseCase<Request, Response>,
        requestDto: RequestDto,
        requestConverter: (RequestDto) -> Request,
        responseConverter: (Response) -> ResponseDto
    ): Single<ResponseDto> {
        return Single.just(requestConverter(requestDto))
            .flatMap { req: Request ->
                useCase.execute(req)
            }
            .flatMap { resp: Response ->
                Single.just(responseConverter(resp))
            }
    }
}
