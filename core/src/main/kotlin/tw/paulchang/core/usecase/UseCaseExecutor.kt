package tw.paulchang.core.usecase

import io.reactivex.rxjava3.core.Single

interface UseCaseExecutor {
    operator fun <Request, Response> invoke(
        useCase: UseCase<Request, Response>,
        request: Request,
    ): Single<Response>

    operator fun <RequestDto, ResponseDto, Request, Response> invoke(
        useCase: UseCase<Request, Response>,
        requestDto: RequestDto,
        requestConverter: (RequestDto) -> Request,
        responseConverter: (Response) -> ResponseDto
    ): Single<ResponseDto>

    operator fun <RequestDto, Request> invoke(
        useCase: UseCase<Request, Unit>,
        requestDto: RequestDto,
        requestConverter: (RequestDto) -> Request
    ) = invoke(useCase, requestDto, requestConverter, {})

    operator fun invoke(
        useCase: UseCase<Unit, Unit>
    ) = invoke(useCase, Unit, {})

    operator fun <ResponseDto, Response> invoke(
        useCase: UseCase<Unit, Response>,
        responseConverter: (Response) -> ResponseDto
    ) = invoke(useCase, Unit, {}, responseConverter)
}
