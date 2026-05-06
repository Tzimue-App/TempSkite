package com.example.skite.data.error

class ConversionError(val source: String, cause: Throwable?) : 
    AppError("CONVERSION_ERROR", "Failed to convert $source: ${cause?.message}", "", cause)
