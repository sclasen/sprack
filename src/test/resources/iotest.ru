require 'rack'

run lambda { |env|
 begin
  req = Rack::Request.new(env)

  req.body.each { |s|
    puts s
  }

  req.body.rewind

  reader = req.body.read(2)

  rest = req.body.gets

  both = reader + "->" + rest

  req.body.rewind

  [200, {"Content-Type" => "text/plain"}, [both, req.body.gets]]
 rescue StandardError => e
   [500, {}, ["no"]]
 end
}