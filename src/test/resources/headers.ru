require 'bundler/setup'
require 'rack'

run lambda { |env|
 if env['HTTP_ACCEPT'] == 'application/json'
    [200, {"Content-Type" => "text/plain"}, ["Hello. The time is #{Time.now}"]]
 else
    [400, {"Content-Type" => "text/plain"}, ["BOO. The time is #{Time.now}"]]
 end
}