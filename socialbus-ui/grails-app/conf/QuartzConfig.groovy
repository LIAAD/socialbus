/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 4/16/13
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */


quartz {
    autoStartup = true
    jdbcStore = false

}
environments {
    test {
        quartz {
            autoStartup = false
        }
    }
}