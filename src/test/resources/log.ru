require 'rack'

run lambda { |env|
 $stdout.puts("I LOGGED SOMETHING")
 $stderr.puts("I errLOGGED SOMETHING")
 #env['rack.logger'].warn("I rackLOGGED SOMETHING")
 [200, {"Content-Type" => "text/plain"}, ["Hello. The time is #{Time.now}"]]
}
