package contracts

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType;

Contract.make {
    description("Should return all hotels")
    request {
        method("GET")
        url("/hotels")
    }

    response {
        status(HttpStatus.OK.value())
        headers {
            contentType(MediaType.APPLICATION_JSON_VALUE)
        }
        body([[id:"99", name:"Ibiza Hotel"]])
    }
}