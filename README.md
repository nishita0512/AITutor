# AI Tutor

This is a virtual tutor platform for basic education with the goal of improving the learning experience for students. I used AI models such as Confusion Expression Detection and Age Estimation to continuously assess learners’ comprehension levels. The data drove the content curation and presentation dynamically, keeping the content relevant and engaging throughout the learning experience. Integrating these AI capabilities into my Android app through my Flask API, I created chapters based on the user’s chosen topics and adjusted the content complexity based on the estimated age using the Gemini API. I also actively responded to learners’ confusion during reading by offering simpler explanations when needed. All in all, this solution was designed to meet the different learning needs of students with personalized and efficient educational support.

## Requirements

1. AI Tutor API: https://github.com/nishita0512/AI_Tutor_API
2. Gemini API Key

## How to Use

1. First clone the project into your Android Studio IDE
2. Insert the following variables in the "app/src/main/java/com/example/aitutor/util/*Constants*" File:
   val GEMINI_API_KEY = "<GEMINI_API_KEY>"
   val API_URL = "<API_URL>"
3. Run the App
4. Give the Camera Permission When Asked
5. Click on Add button at bottom to Generate Chapters for a new Course
6. Added Courses will be shown on main page

## Screenshots
<img src="https://github.com/nishita0512/AITutor/assets/127613866/2aafc897-fafd-4228-936c-a002d9ddb39c" width="200" height="400" />
<img src="https://github.com/nishita0512/AITutor/assets/127613866/d54610ff-af8d-4e0b-b143-e63913030c40" width="200" height="400" />
<img src="https://github.com/nishita0512/AITutor/assets/127613866/4d32925a-74c4-417a-8d9c-bb9a93c56adc" width="200" height="400" />
<img src="https://github.com/nishita0512/AITutor/assets/127613866/e324b85f-8761-49e7-85ce-7bf7173508dc" width="200" height="400" />
<img src="https://github.com/nishita0512/AITutor/assets/127613866/4a7ccead-bc14-4e05-a04d-3d18c5818133" width="200" height="400" />
<img src="https://github.com/nishita0512/AITutor/assets/127613866/aa4a8818-44dd-46b0-bd19-5b76477a49a9" width="200" height="400" />
<img src="https://github.com/nishita0512/AITutor/assets/127613866/38486357-abf4-42d4-89b0-61086dc6173d" width="200" height="400" />
