Lab 103: Customize Keycloak Website Token
----

In this lab we want to add a custom claim to the the token contents generated by keycloak for the `labs-keycloak-website-student` client.

# Instructions

## Start Keycloak Environment
> Start Keycloak Environment unless running.  
> Run `java start.java` in the root of the project.

## Open Keycloak Admin UI

Keycloak Admin Console: http://localhost:9090/auth

Username: `admin`
Password: `admin`

## Select the Keycloak Spring Labs realm

Make sure that you select the "Labs" realm in the upper left corner.

![img_1.png](img_1.png)

## Select the `labs-keycloak-website-student` Client 

![img_6.png](img_6.png)

## Open Client Scopes in the Client Details

![img_7.png](img_7.png)

## Add dedicated Protocol Mapper to Client 

Click on "labs-keycloak-website-student-dedicated".

![img_dedicated_mappers.png](img_dedicated_mappers.png)

Then click "Configure new Mapper".

![img_add_dedicated_mapper.png](img_add_dedicated_mapper.png)

Select "Hardcoded Claim"

![img_5.png](img_5.png)

Then enter the following information:

- Mapper Name: `conference`
- Token Claim Name: `conference`
- Claim Value: `Spring I/O 2024`
- Click Save

![img_add_mapper_conference.png](img_add_mapper_conference.png)

Now go back to the Client Scopes Configuration for this Client.

## Click on Evaluate Tab

![img_3.png](img_3.png)

Then select evaluate and enter the username `user`.

![img_8.png](img_8.png)

You can now generate the content for an Access Token, ID Token and UserInfo.

![img_4.png](img_4.png)

See that the access token now contains our custom `conference` claim.

![img_9.png](img_9.png)

# Summary

Congratulations you now know how to customize token contents for a client with the Keycloak Admin Console.
