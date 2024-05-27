Lab 401: Customize Keycloak Login Theme
---

In this lab we want to customize the Keycloak login theme used by the labs realm.

# Instructions

## Start Keycloak Environment
> Start Keycloak Environment unless running.  
> Run `java start.java` in the root of the project.

## Open Keycloak Admin UI

Keycloak Admin Console: http://localhost:9090/auth

Username: `admin`
Password: `admin`

## Change longin theme

In the realm settings go to "Themes".

Select the login theme "lab".

![img.png](img.png)

## Open Account client

To see the selected login theme in action.  

Click on clients then search for the `account-console` client.  

Open the `Home URL` link for the `account-console` client.

![img_account_console_link.png](./img_account_console_link.png)

## Fix broken logo

![img_1.png](img_1.png)

The logo for our custom theme is not displayed fix the logo in [lab/login/resources/css/custom-login.css](lab/login/resources/css/custom-login.css).

Reload the login page again.

![img_2.png](img_2.png)

# Summary

Congratulations, you learned how to customize the Keycloak login theme!