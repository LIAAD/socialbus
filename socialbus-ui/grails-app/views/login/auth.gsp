<html>
<head>
    <meta name='layout' content='login'/>
    <title><g:message code="springSecurity.login.title"/></title>
    %{--<style type='text/css' media='screen'>--}%
    %{--#login {--}%
    %{--margin: 15px 0px;--}%
    %{--padding: 0px;--}%
    %{--text-align: center;--}%
    %{--}--}%

    %{--#login .inner {--}%
    %{--width: 340px;--}%
    %{--padding-bottom: 6px;--}%
    %{--margin: 60px auto;--}%
    %{--text-align: left;--}%
    %{--border: 1px solid #aab;--}%
    %{--background-color: #f0f0fa;--}%
    %{---moz-box-shadow: 2px 2px 2px #eee;--}%
    %{---webkit-box-shadow: 2px 2px 2px #eee;--}%
    %{---khtml-box-shadow: 2px 2px 2px #eee;--}%
    %{--box-shadow: 2px 2px 2px #eee;--}%
    %{--}--}%

    %{--#login .inner .fheader {--}%
    %{--padding: 18px 26px 14px 26px;--}%
    %{--background-color: #f7f7ff;--}%
    %{--margin: 0px 0 14px 0;--}%
    %{--color: #2e3741;--}%
    %{--font-size: 18px;--}%
    %{--font-weight: bold;--}%
    %{--}--}%

    %{--#login .inner .cssform p {--}%
    %{--clear: left;--}%
    %{--margin: 0;--}%
    %{--padding: 4px 0 3px 0;--}%
    %{--padding-left: 105px;--}%
    %{--margin-bottom: 20px;--}%
    %{--height: 1%;--}%
    %{--}--}%

    %{--#login .inner .cssform input[type='text'] {--}%
    %{--width: 120px;--}%
    %{--}--}%

    %{--#login .inner .cssform label {--}%
    %{--font-weight: bold;--}%
    %{--float: left;--}%
    %{--text-align: right;--}%
    %{--margin-left: -105px;--}%
    %{--width: 110px;--}%
    %{--padding-top: 3px;--}%
    %{--padding-right: 10px;--}%
    %{--}--}%

    %{--#login #remember_me_holder {--}%
    %{--padding-left: 120px;--}%
    %{--}--}%

    %{--#login #submit {--}%
    %{--margin-left: 15px;--}%
    %{--}--}%

    %{--#login #remember_me_holder label {--}%
    %{--float: none;--}%
    %{--margin-left: 0;--}%
    %{--text-align: left;--}%
    %{--width: 200px--}%
    %{--}--}%

    %{--#login .inner .login_message {--}%
    %{--padding: 6px 25px 20px 25px;--}%
    %{--color: #c33;--}%
    %{--}--}%

    %{--#login .inner .text_ {--}%
    %{--width: 120px;--}%
    %{--}--}%

    %{--#login .inner .chk {--}%
    %{--height: 12px;--}%
    %{--}--}%
    %{--</style>--}%
</head>

<body>
<section id='login'>
    <div class='inner' style="text-align: center">

        %{--<div class="navbar navbar-fixed-top" style="background-color: #888; color:white">--}%
        %{--<h1>TwitterEcho</h1>--}%
        %{--</div>--}%

        %{--<hr/>--}%

		<h3>welcome to socialecho</h3>
        <h2>please sign in</h2>

        <g:if test='${flash.message}'>
            <div class='alert alert-error'>${flash.message}</div>
        </g:if>

        <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
            <p>

            <h3 for='username'><g:message code="springSecurity.login.username.label"/>:</h3>
            <input type='text' class='text_' name='j_username' id='username'/>
        </p>

            <p>

            <h3 for='password'><g:message code="springSecurity.login.password.label"/>:</h3>
            <input type='password' class='text_' name='j_password' id='password'/>
        </p>

            <p>
                <button type="submit" class="btn btn-primary btn-large">
                    Sign in as admin
                </button>
            </p>
			or
			
			<p>
			<twitterAuth:button/>
			</p>
        </form>

    </div>
</section>


<script type='text/javascript'>
    <!--
    (function () {
        document.forms['loginForm'].elements['j_username'].focus();
    })();
    // -->
</script>
</body>
</html>
