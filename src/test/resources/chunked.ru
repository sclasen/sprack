require 'rack'

run lambda { |env| [200, {"Content-Type" => "text/plain", "Transfer-Encoding" => "chunked"}, ["H", "e", "l", "l", "o"]] }