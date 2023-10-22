package com.example.feauteres.controllers

import com.example.feauteres.model.*
import com.example.feauteres.controllers.ImagesController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class FetchUserpublicId(val id: String)

@Serializable
data class FetchUserpublicSex(val sex: String)

@Serializable
data class UserpublicRemote(val userpublicid: String, val name: String, val description: String, val location: String, val age: String, val sex: String, val imagesId: List<ImagesResponse> )

@Serializable
data class UserpublicResponse(var userspublic: List<UserpublicRemote>)

class UserpublicController(private val call: ApplicationCall) {

    suspend fun fetchUserpublicSex(): UserpublicResponse? {
        print("fetchUserpublicSex() START \n")
        val receive = call.receive<FetchUserpublicSex>()
        val users = UserspublicModel.fetchOnSex(receive.sex)

        if (users != null) {
            
            // пересложи заново
            // return UserpublicResponse(userspublic = users.map {   
                
            //     val imagesResponseList = ImagesModel.fetchWithUserspublicid(it.id).let {it as? List<ImagesDTO>} ?: emptyList()
            //     var imagesIdsList: List<String> = emptyList()
            
            //     // if imagesResponseList != null {
            //         imagesResponseList.forEach {
            //             imagesIdsList.add(it.id)
            //         }
            //     // }
                
            //     UserpublicRemote(userpublicid = it.id.toString(), 
            //                     name = it.name,
            //                     description = it.description,
            //                     location = it.location,
            //                     age = it.age.toString(),
            //                     sex = it.sex,
            //                     imagesId = imagesIdsList)
            // })

            
            
            // var userpublicRemote = UserpublicResponse(userspublic = emptyList())
            // users.forEach {
                
            //     var imagesResponseList: List<ImagesResponse> = emptyList()
            //     imagesResponseList = ImagesModel.fetchWithUserspublicid(it.id).let {it as? List<ImagesResponse>} ?: emptyList()
                
            //     userpublicRemote.userspublic += UserpublicRemote(userpublicid = it.id.toString(), 
            //                     name = it.name,
            //                     description = it.description,
            //                     location = it.location,
            //                     age = it.age.toString(),
            //                     sex = it.sex,
            //                     imagesId = imagesResponseList)

            // }
            // println(" userpublicRemote.userpublic.count = ${userpublicRemote.userspublic.count().toString()}")
            var userpublicRemote: UserpublicResponse = UserpublicResponse(userspublic = emptyList())
             userpublicRemote.userspublic = users.map {
                val imagesResponseList = ImagesController().getIdAllImagesOfUser(it.id.toString())?.let {it as? List<ImagesResponse>} ?: emptyList()
                UserpublicRemote(userpublicid = it.id.toString(), 
                                name = it.name,
                                description = it.description,
                                location = it.location,
                                age = it.age.toString(),
                                sex = it.sex,
                                imagesId = imagesResponseList)
            }

            return userpublicRemote
        } else {return null}

        

    }
    
}