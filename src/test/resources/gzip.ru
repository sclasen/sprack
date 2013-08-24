use Rack::Deflater

run lambda { |env| [200, {"Content-Type" => "text/plain"}, ["Hello. Hows the GZIP workin out?"]] }