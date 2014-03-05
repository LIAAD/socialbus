package pt.up.fe



class TrendingTopicsJob {
    
    static triggers = {
/*        cron name: 'myTrigger', cronExpression: "0/30 * * * ?", startDelay: 10000*/
		simple repeatInterval: 1000l * 60 * 60, startDelay: 10000 // execute job once in 5 seconds
    }

    def group = "twitter-mapreduces"
	
    def concurrent = false

	def trendingTopicsService

    def execute() {
        // execute job
		
/*		trendingTopicsService.execute();*/
    }
}
