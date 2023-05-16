package com.mercadolivro.events

import com.mercadolivro.model.PurchaseModel
import org.springframework.context.ApplicationEvent

class PurchaseEvent(
    val source: Any,
    val purchaseModel: PurchaseModel,
) : ApplicationEvent(source) {

}