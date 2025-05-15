# Project Details

Final Project: CanGo

Group Members: Aarav Kumar (akuma143), Chris Ho (clho), Grace Wang (gwang71), and Nathan Phan (ncphan)

Estimated Time: 100 hours (split 4 ways)

Github Link: https://github.com/cs0320-s25/term-project-cangaroo

Our project (CanGo) is a social platform designed to foster in-person social connections. We believe our app 
is really differentiated from our competitors in a couple of ways, allowing the best of both worlds in terms of allowing you to keep in touch with your existing friends while offering opportunities to discover new events and make new friends. CanGo will initally be designed to launch in a university setting.


# Design Choices

Our app comes with a couple of unique design choices that combine the best of a university based event planner (ie. Events @Brown), a public facing event recommender (ie. Facebook), and coordinating with close friends (ie. a group chat).


The privacy/security a university based event planner provides (ie. Events @Brown): Anyone can make a new event. This is unlike university event calenders which restrict to approved organizations. We wanted to facillitate all types of social experiences, like getting together to watch a sports game not just official events. To keep our app secure, only people with official university email addresses can use our app, and they must use their real name.

Custom recognition like a public facing event recommender (ie. Facebook): Our app comes with a custom recommend feature. This allows the app to dynamically recommend events to you based on your interested tags and organizations.

Easily keep in the loop with close friends (ie. coordinating via a group chat): Our app also allows you to "friend" your close friends. This will allow you to see which events they are attending and even filter events by attendence. 


# Errors/Bugs

n/a

# Tests

Frontend Playwright: In the client folder, we have a test subfolder. Inside that is App.spec.ts. We have a variety of tests that ensure that the correct frontend elements appear at the correct times. 

Backend Junit: In the server folder, there is a test subfolder with a variety of backend tests. We especially test the matching and search algorithms across many cases to ensure that they are as dynamic and customized as possible. 

Postman: We have a variety of tests to test our application's api calls, including creating and editing events, the search/match algorithms, friending people, and so much more. 

# How to

Launch the program: 

1. Open your terminal. Next, you should type "cd server", then "mvn package", and lastly "./run". Ensure you get confirmation the server is running.

2. Open a new terminal. Next, you should type "cd client" and then "npm start". Copy the subsequent link into your preferred browser.

3. Sign in using a Brown University email. 


Tests:
To run the Playwright tests, you can type "cd client" in the terminal and run "npx run test". 
To run the backend tests, you can run the test by clicking the play button on the file.


# Collaboration

* OpenAI. (2025). ChatGPT (GPT-4o) [Large language model]. https://chat.openai.com/chat
    * Generated 20 random events to be able to tests
