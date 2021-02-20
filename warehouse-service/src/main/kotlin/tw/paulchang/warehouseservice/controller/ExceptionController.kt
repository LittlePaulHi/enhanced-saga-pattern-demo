package tw.paulchang.warehouseservice.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.usecase.exception.NotFoundException

@ControllerAdvice
@RestController
class ExceptionController {
    @ExceptionHandler(NotFoundException::class)
    fun notFound(ex: NotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity(mapOf("message" to ex.localizedMessage), HttpStatus.NOT_FOUND)
    }
}
