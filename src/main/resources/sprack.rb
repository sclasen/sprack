require 'java'
require 'rack'
require 'rack/rewindable_input'
require 'stringio'
require 'logger'

module Sprack

  module RackServer
    class Builder
      def build(filename, port, out, err)
        rack_app, options_ignored = Rack::Builder.parse_file filename
        return SprayAdapter.new(rack_app, port, out, err)
      end
    end

    class SprayAdapter
      def initialize(app, port, out, err)
        @app = app
        @port = port.to_s
        @out = out
        @err = err
        @errors = err
        @logger = ::Logger.new(out)
        $stdout = @out
        $stderr = @err
      end

      def call(request)

        rack_env = {
            'rack.version' => Rack::VERSION,
            'rack.multithread' => true,
            'rack.multiprocess' => false,
            'rack.input' => StringIO.new(request.input.utf8String),   #StringIO is a Rack Compliant IO I think
            'rack.errors' => @errors,
            'rack.logger' => @logger,
            'rack.url_scheme' => request.scheme,
            'REQUEST_METHOD' => request.method,
            'SCRIPT_NAME' => '',
            'PATH_INFO' => request.path,
            'REQUEST_PATH' => request.path,
            'REQUEST_URI' => request.path,
            'QUERY_STRING' => (request.query || ""),
            'SERVER_NAME' => @host,
            'SERVER_PORT' => @port,
            "SERVER_PROTOCOL"=>"HTTP/1.1",
            "SERVER_SOFTWARE" => "sprack"
        }

        rack_env['CONTENT_TYPE'] = request.content_type unless request.content_type.nil?
        rack_env['CONTENT_LENGTH']  = request.content_length unless request.content_length.nil?

        rack_env.merge!(request.headers)

        response_status, response_headers, response_body = @app.call(rack_env)

        spray_headers = []
        response_headers.each do |name, value|
          spray_headers << Java::spray::http::HttpHeaders::RawHeader.apply(name,value)
        end

        body_array = []
        response_body.each{|p| body_array << p.to_java_bytes }
        body = Java::java::util::Arrays.asList(body_array.to_java)
        headers = Java::java.util::Arrays.asList(spray_headers.to_java)

        [response_status.to_java, headers, body]

      end
    end


  end
end

