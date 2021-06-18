# Enhanced Saga Pattern Demo

[![Build Status](https://travis-ci.com/LittlePaulHi/orchestration-microservices.svg?token=7FC6NXiT4KgztzxgaUgr&branch=develop)](https://travis-ci.com/LittlePaulHi/orchestration-microservices)

## Table of Contents

- [Enhanced Saga Pattern Demo](#enhanced-saga-pattern-demo)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Prerequisites](#prerequisites)
  - [Build and Run](#build-and-run)
  - [Architecture and Design](#architecture-and-design)
    - [System Architecture and Tech stacks](#system-architecture-and-tech-stacks)
    - [Software Design](#software-design)
  - [Project Structure](#project-structure)
    - [`core` module](#core-module)
    - [`orchestrator` module](#orchestrator-module)
    - [Microservices Event Workflow](#microservices-event-workflow)
  - [References](#references)

## Overview

This project is a demonstration for the paper; it includes a simplified e-commerce back-end application composed of several domains: customer, warehouse, order, billing, and shipping.

The paper mentioned that the saga pattern lacks the ACID's isolation; the issue has also been discussed in the [post](https://developers.redhat.com/blog/2018/10/01/patterns-for-distributed-transactions-within-a-microservices-architecture), from the Red Hat developer blog.

This project has implemented the methods, which was proposed from that paper, the `quota-cache` and `eventual commit-sync service`. We have applied these proposed methods to the above-mentioned e-commerce application, and also keep the pure baseline version via the different consumer.

## Prerequisites

You have to take a quick look at the [Spring Boot](https://spring.io/projects/spring-boot) and [RxJava](https://github.com/ReactiveX/RxJava) if you want to join the development of this project.

Before compiling, you need to deploy the following services and config its to the corressponding environment variables (e.g. `POSTGRES_CUSTOMER_URL`):

- [Apache Kafka](https://kafka.apache.org/)
- [PostgresSQL](https://www.postgresql.org/)
- [Redis](https://redis.io/)

## Build and Run

Build the application first to check everything is fine (make sure that `Docker` was already installed)

```bash
./gradlew clean build
```

And run it with gradle locally

```bash
./gradlew bootRun
```

> The project development is based on [Spring Boot](https://spring.io/projects/spring-boot)

## Architecture and Design

### System Architecture and Tech stacks

![image](doc/img/Orchestration-based%20architecture.png)

This system was implemented by the mixed version of orchestration and event choreography techniques, there has an `orchestrator` module to manage all the microservices and deal with both failure event and completion event via the message queue middleware.

We use Apache Kafka to be our message queue middle, the open source distributed event streaming platform, which can be used to publish and subscribe to the streams of messages. Kafka is able to build the high throughput application, it is capable to handle thousands of mes­ sages per second, also, Kafka has the durability characteristic, it always stores the messages on the disk for persistence.

### Software Design

The whole project’s implementation is based on the [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) approach, we have defined all the domain use cases in advance, for example, the `billing-service` has the validate­-payment-­use­case, payment-­pay­-use­case, revert­payment­pay-­use­case, etc.

## Project Structure

There are 6 modules in this project, which is, `core`, `orchestrator`, `customer-service`, `warehouse-service`, `order-service`, `billing-service`, and `shipping-service`.

### `core` module

We have defined all the domain entities in this module, and there are almost 0 dependencies to frameworks and libraries except RxJava3.

The following tree structure of `core` module shows use-cases for the domains and also includes the Kafka topic model and request DTO.

```bash
.
└── core
    ├── dto
    │   ├── billing
    │   │   ├── AddPaymentRequestDto.kt
    │   │   ├── CreateBillingRequestDto.kt
    │   │   ├── GetAllProductsByIdsResponseDto.kt
    │   │   ├── PaymentPayRequestDto.kt
    │   │   └── ValidatePaymentRequestDto.kt
    │   ├── customer
    │   │   ├── AddCartRequestDto.kt
    │   │   └── PurchaseRequestDto.kt
    │   ├── order
    │   │   ├── InitOrderRequestDto.kt
    │   │   └── PlaceOrderRequestDto.kt
    │   ├── shipping
    │   │   └── DispatchShippingRequestDto.kt
    │   └── warehouse
    │       ├── FetchGoodsRequestDto.kt
    │       └── GetAllProductsByIdsRequestDto.kt
    ├── entity
    │   ├── billing
    │   │   ├── Billing.kt
    │   │   └── Payment.kt
    │   ├── customer
    │   │   └── Cart.kt
    │   ├── order
    │   │   └── Order.kt
    │   ├── shipping
    │   │   └── Shipping.kt
    │   └── warehouse
    │       ├── Product.kt
    │       └── Warehouse.kt
    ├── model
    │   ├── FinishedBuyEventTopicModel.kt
    │   ├── KafkaTopics.kt
    │   └── PurchaseTopicModel.kt
    └── usecase
        ├── UseCase.kt
        ├── UseCaseExecutor.kt
        ├── UseCaseExecutorImp.kt
        ├── billing
        │   ├── AddPaymentUseCase.kt
        │   ├── CreateBillingUseCase.kt
        │   ├── PaymentPayUseCase.kt
        │   ├── RevertPayUseCase.kt
        │   └── ValidatePaymentUseCase.kt
        ├── customer
        │   └── PurchaseUseCase.kt
        ├── exception
        │   └── NotFoundException.kt
        ├── order
        │   ├── InitOrderUseCase.kt
        │   └── PlaceOrderUseCase.kt
        ├── repository
        │   ├── BillingRepository.kt
        │   ├── OrderRepository.kt
        │   ├── PaymentRepository.kt
        │   ├── ProductRepository.kt
        │   ├── ShippingRepository.kt
        │   └── WarehouseRepository.kt
        ├── shipping
        │   └── DispatchShippingUseCase.kt
        └── warehouse
            ├── FetchGoodsFromOrderUseCase.kt
            ├── GetAllProductsByIdsUseCase.kt
            ├── GetProductByIdUseCase.kt
            └── RevertFetchGoodsUseCase.kt
```

### `orchestrator` module

In the `orchestrator` module, we have configured all the microservices to the corresponding [WebClient](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client), and organize them via the RxJava library, which composing asynchronous and event-based programs by using the observer pattern.

Due to the orchestrator module, we can easily manage all the microservices, and also can adjust the workflow at any time if needed.

### Microservices Event Workflow

The workflow among domains/microservices is shown as following:

![image](doc/img/Event%20workflow.png)

Once `customer-service` produce the purchase event to the corresspoding message queue:

1. `warehouse-service` fetches goods
2. `order-service` initializes an empty order and marked it as IN-PROGRESS or marked as FAILED if it cannot fetch any goods
3. `billing-service` validates the specified payment
4. `billing-service` will collect the payment if validation successful; otherwise, terminate the flow and mark the order as FAILED
5. `shipping-service` dispatch the delivery
6. `order-service` completes an order, updates the info that includes order status, shipping-id, and amount

## References

- [Patterns for distributed transactions within a microservices architecture](https://developers.redhat.com/blog/2018/10/01/patterns-for-distributed-transactions-within-a-microservices-architecture)
- [Web on Reactive Stack - Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [ReactiveX](http://reactivex.io/)
- [Apache Kafka Docker](https://github.com/wurstmeister/kafka-docker)
- [A simple clean architecture example in Kotlin and Spring Boot 2.0](https://github.com/aantoniadis/clean-architecture-example)
- [Domain-driven design example in Kotlin with Spring framework](https://github.com/ttulka/ddd-example-ecommerce-kotlin)
