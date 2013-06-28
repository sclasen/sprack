require 'bundler/setup'
require 'rack'

run lambda { |env|
 $stdout.puts("I LOGGED SOMETHING")
 [200, {"Content-Type" => "text/plain"}, ["Hello. The time is #{Time.now}"]]
}