package com.example.pandora

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse>
    
    @GET("images.php")
    suspend fun getImages(): Response<ImagesResponse>
    
    @POST("images.php")
    suspend fun uploadImage(@Body request: ImageRequest): Response<ApiResponse>
    
    @PUT("images.php")
    suspend fun updateImage(@Body request: UpdateImageRequest): Response<ApiResponse>
    
    @DELETE("images.php")
    suspend fun deleteImage(@Query("id") id: Int): Response<ApiResponse>
    
    @POST("purchase.php")
    suspend fun purchaseImage(@Body request: PurchaseRequest): Response<PurchaseResponse>
    
    @GET("saldo.php")
    suspend fun getSaldo(@Query("email") email: String): Response<SaldoResponse>
}

// Data classes untuk request
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val name: String, val password: String, val level: String = "user")
data class ImageRequest(val image_path: String, val image_name: String, val image_price: String)
data class UpdateImageRequest(val id: Int, val image_name: String, val image_price: String)

// Data classes untuk response
data class LoginResponse(val success: Boolean, val message: String, val user: User?)
data class ApiResponse(val success: Boolean, val message: String)
data class ImagesResponse(val success: Boolean, val data: List<ImageData>)

// Model classes
data class User(val email: String, val name: String, val level: String, val saldo: Int)
data class ImageData(val id: Int, val image_path: String, val image_name: String, val image_price: String)

// Tambahkan data classes baru
data class PurchaseRequest(val email: String, val image_id: Int)
data class PurchaseResponse(val success: Boolean, val message: String, val new_saldo: Int?, val purchased_item: String?)
data class SaldoResponse(val success: Boolean, val saldo: Int?, val message: String?)
