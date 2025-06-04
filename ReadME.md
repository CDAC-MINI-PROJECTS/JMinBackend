# RUN Spring Boot Project and Try Below API 

- https://malshani-wijekoon.medium.com/spring-boot-folder-structure-best-practices-18ef78a81819

Routes 
Base Route:  http://localhost:8080/api

features needs to add
- logging
- seperate entity and dto and calling serivce into controller
- validation
- swagger
- Exception Handler


Auth
 /auth/register -> POST
 /auth/login -> POST
 
User 
 /users -> POST 
 /users -> GET 
 /users -> PUT
 /users -> DELETE
 / 
 /users/me -> GET
 Dream 
 /dreams -> POST 
 /dreams -> GET 
 /dreams -> DELETE
 /dreams -> PUT
 /dreams/like -> PUT
 /dreams/repost -> PUT
 /dreams/repost/{username} -> GET
 /dreams/tagged/{username} -> GET
 /dreams/feed/{userId} -> GET

 Comments

 /comment -> GET
 /comment -> POST
 /comment -> PUT
 /comment -> DELETE
 /comment/reaction/{commentID} -> PUT
 /comment/dreams/{dreamsID} -> GET


 Follow
 /follower/{userID} -> GET
 /following/{userID} -> GET
 /follow -> POST -> First Time
 /unfollow -> PUT
 

analytics

# Note -> Create register and login first and use token for authorization of other apis

