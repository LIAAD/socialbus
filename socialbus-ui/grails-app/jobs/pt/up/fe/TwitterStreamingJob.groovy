package pt.up.fe



class TwitterStreamingJob {
    static triggers = {
        cron name: 'myTrigger', cronExpression: "0 0/3 * * * ?", startDelay: 10000
    }

    def group = "twitter-streaming-consumer"
    def concurrent = false

    def execute() {
        print "Job run!"
    }
}
