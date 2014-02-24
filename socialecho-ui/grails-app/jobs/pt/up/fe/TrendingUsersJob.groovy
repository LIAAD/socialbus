package pt.up.fe



class TrendingUsersJob {
    static triggers = {
		simple repeatInterval: 1000l * 60 * 60, startDelay: 30000 // execute job once in 5 seconds
    }

    def group = "twitter-mapreduces"
	
    def concurrent = false

	def trendingUsersService

    def execute() {
        // execute job
		
		trendingUsersService.execute();
    }
}
